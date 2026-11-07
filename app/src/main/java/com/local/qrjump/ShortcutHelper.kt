package com.local.qrjump

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat

/**
 * 把设备簿同步成桌面快捷方式，并支持「放到桌面」。
 *
 * ── 为什么做这个 ──
 * 目标是把「开支付宝 → 搜小程序 → 点扫一扫」压成尽量少的动作。
 * 设备列表已经省掉了"对准机身码"这一步，但还要：打开 App → 点一行。
 * 桌面上一个独立图标直接选中某台机器，再省掉"打开 App + 点一行"两步。
 *
 * ── 实测结论（2026-11-06 23:28–23:45，Redmi K40S / MIUI V816 / Android 14）──
 * 两条快捷方式通道在这台机器上都不可用，所以「放到桌面」最终落到小部件：
 *
 *   ① [pinViaPinApi]：requestPinShortcut 返回 true 但桌面什么都不画（MIUI 假成功）。
 *   ② 旧版 com.android.launcher.action.INSTALL_SHORTCUT 广播：
 *      MIUI 的 InstallShortcutReceiver 虽然还注册着，但广播刚发出就被系统丢掉 ——
 *      `W/ActivityManager: Broadcast com.android.launcher.action.INSTALL_SHORTCUT
 *       no longer supported. It will not be delivered.`
 *      这是 Android 框架层面的拦截，不是 MIUI 的锅，所以 android 14+ 上留着它也是死代码，
 *      已整段删掉（原先的 [findLegacyReceiver] / 清单里的两条 launcher 权限一并移除）。
 *
 * ── 安全边界 ──
 * 快捷方式只携带**设备簿里的键**（`SN|862270...`），不携带 URL，
 * 也不携带任何 token。落点 [ShortcutActivity] 会拿这个键回设备簿里查，
 * 查不到就什么都不做；查到也会重新跑一遍 [ParseResult.parse] 完整校验。
 * 所以快捷方式的存在不扩大任何攻击面。
 */
object ShortcutHelper {

    /** 最多发布这么多个快捷方式（系统对动态快捷方式数量有上限，一般 5 个左右安全）。 */
    private const val MAX_SHORTCUTS = 4

    // ------------------------------------------------------------ 动态快捷方式

    /**
     * 按当前设备簿重建「长按图标」的动态快捷方式。设备为空时清空（不留陈旧的僵尸入口）。
     *
     * 注意：MIUI 不展示这一层，所以这条对 MIUI 用户基本是空转；
     * 保留它是为了在原生/第三方桌面上仍然好用，成本只有一次 binder 调用。
     */
    fun sync(context: Context) {
        val devices = DeviceStore.all(context)

        if (devices.isEmpty()) {
            val removed = try {
                ShortcutManagerCompat.removeAllDynamicShortcuts(context)
            } catch (t: Throwable) {
                QLog.w("Shortcut", "清空快捷方式失败：${QLog.describe(t)}")
                false
            }
            QLog.i("Shortcut", "设备簿为空，已清空快捷方式（removed=$removed）")
            return
        }

        val list = devices.take(MAX_SHORTCUTS).map { d ->
            val label = labelOf(d)
            ShortcutInfoCompat.Builder(context, d.key)
                .setShortLabel(label)
                .setLongLabel(label)
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground))
                .setIntent(shortcutIntent(context, d))
                .build()
        }

        val ok = try {
            ShortcutManagerCompat.setDynamicShortcuts(context, list)
            true
        } catch (t: Throwable) {
            QLog.w("Shortcut", "发布快捷方式失败：${QLog.describe(t)}")
            false
        }
        QLog.i(
            "Shortcut",
            "已同步快捷方式 ${list.size} 个（成功=$ok）：" +
                list.joinToString { it.shortLabel.toString() }
        )
    }

    // ------------------------------------------------------------ 放到桌面

    /**
     * 把某台设备放到桌面。返回 true 表示「至少有一条通道被系统接受了」。
     *
     * 之所以要返回值，是因为以前这里只有一条路，而那条路在 MIUI 上
     * fake-succeed（返回 true 却不画图标），用户根本无从判断。
     */
    fun pinToHome(context: Context, d: DeviceStore.Device): Boolean {
        val label = labelOf(d)

        // MIUI 的启动器对 requestPinShortcut 是"假成功"：返回 true，桌面什么都不画。
        // 既然已经知道它不认，就别再假装成功 —— 直接让调用方去走小部件，
        // 免得用户回桌面找半天找不到，以为应用坏了。
        if (isMiuiLauncher(context)) {
            QLog.w("Shortcut", "当前桌面是 MIUI（${homePackage(context)}），pin API 实测假成功，跳过")
            return false
        }

        if (pinViaPinApi(context, d, label)) {
            QLog.i("Shortcut", "官方 pin API 已接受")
            return true
        }

        QLog.w("Shortcut", "官方 pin API 未被接受，应改用桌面小部件方案")
        return false
    }

    /**
     * 请求把这**一台**设备放到桌面（1×1 方块）。
     *
     * ── 为什么最终走这条路 ──
     * 实测（Redmi K40S / MIUI V816 / Android 14）两条快捷方式通道全废：
     * requestPinShortcut 返回 true 但不画图；INSTALL_SHORTCUT 广播被
     * ActivityManager 直接丢弃。而小部件是**系统级的另一套机制**
     * （AppWidgetService），MIUI 完整实现了它，从选择器里能正常拖出来。
     *
     * ── 怎么把「哪个方块 = 哪台设备」传下去 ──
     * requestPinAppWidget 的 extras 不会直接进 Provider，所以先把设备键
     * 记到 [WidgetBinding] 的 pending 槽，系统随后拉起
     * [WidgetConfigActivity] 时它取走 pending、绑好、立刻返回 ——
     * 用户看到的就是"点一下，确认添加"，不用再选设备。
     *
     * @return true 表示系统接受了请求（可能仍弹确认框等用户点）。
     */
    fun pinWidget(context: Context, d: DeviceStore.Device): Boolean {
        WidgetBinding.setPendingPin(context, d.key)
        QLog.i("Shortcut", "请求放置单设备小部件：key=${d.key} label=${labelOf(d)}")

        val provider = ComponentName(context, DeviceWidgetProvider::class.java)
        val mgr = try {
            android.appwidget.AppWidgetManager.getInstance(context)
        } catch (t: Throwable) {
            QLog.w("Shortcut", "取 AppWidgetManager 失败：${QLog.describe(t)}")
            WidgetBinding.takePendingPin(context)
            null
        }
        if (mgr == null) {
            QLog.w("Shortcut", "这台机器没有 AppWidgetManager，无法放小部件")
            return false
        }
        if (!mgr.isRequestPinAppWidgetSupported) {
            QLog.w("Shortcut", "桌面声明不支持 requestPinAppWidget，请手动长按桌面 → 添加小部件")
            return false
        }
        val ok = try {
            mgr.requestPinAppWidget(provider, null, null)
        } catch (t: Throwable) {
            QLog.w("Shortcut", "requestPinAppWidget 抛错：${QLog.describe(t)}")
            false
        }
        if (!ok) {
            // 请求没被接受就别把 pending 留在那儿，否则下次手动拖小部件
            // 会被这次的残留值误导，绑到一台并不想要的设备上。
            WidgetBinding.takePendingPin(context)
        }
        QLog.i("Shortcut", "requestPinAppWidget：provider=$provider 结果=$ok")
        return ok
    }

    /** 当前默认桌面是哪个包。查不到返回 null。 */
    private fun homePackage(context: Context): String? {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        return try {
            context.packageManager.resolveActivity(intent, 0)?.activityInfo?.packageName
        } catch (t: Throwable) {
            QLog.w("Shortcut", "查默认桌面失败：${QLog.describe(t)}")
            null
        }
    }

    private fun isMiuiLauncher(context: Context): Boolean =
        homePackage(context)?.startsWith("com.miui.home") == true

    /** 官方 Android 8.0+ 通道。 */
    private fun pinViaPinApi(context: Context, d: DeviceStore.Device, label: String): Boolean {
        val info = ShortcutInfoCompat.Builder(context, "${d.key}#pinned")
            .setShortLabel(label)
            .setLongLabel(label)
            .setIcon(IconCompat.createWithResource(context, R.drawable.ic_launcher_foreground))
            .setIntent(shortcutIntent(context, d))
            .build()

        val ok = try {
            ShortcutManagerCompat.requestPinShortcut(context, info, null)
        } catch (t: Throwable) {
            QLog.w("Shortcut", "requestPinShortcut 抛错：${QLog.describe(t)}")
            false
        }
        QLog.i("Shortcut", "requestPinShortcut：key=${d.key} label=$label 结果=$ok")
        return ok
    }

    private fun shortcutIntent(context: Context, d: DeviceStore.Device): Intent =
        Intent(context, ShortcutActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(ShortcutActivity.EXTRA_KEY, d.key)
            // 让每个设备的快捷方式各有各的任务栈语义，避免互相顶替
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

    private fun labelOf(d: DeviceStore.Device): String =
        if (d.alias.isNotBlank()) d.alias else shortLabel(d)

    /** 默认短名：只有 SN 时用后 6 位，避免桌面图标下面一行放不下 15 位数字。 */
    private fun shortLabel(d: DeviceStore.Device): String = if (d.idKind == ParseResult.ID_SN) {
        "饮水器 …" + d.id.takeLast(6)
    } else {
        "热水器 …" + d.id.take(4)
    }
}
