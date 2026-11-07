package com.local.qrjump

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.accessibility.AccessibilityManager

/**
 * 无障碍授权的「自愈」。
 *
 * ── 为什么需要它 ──────────────────────────────────────────────
 * 22:33 在真机上把三种杀进程方式逐个试了一遍（Redmi K40S / MIUI OS1.0 / Android 14）：
 *
 *   | 方式                        | accessibility_enabled | enabled_accessibility_services |
 *   |-----------------------------|-----------------------|--------------------------------|
 *   | `am kill`（普通杀进程）      | 1（保持）             | 保持，服务自动重绑             |
 *   | 从最近任务划掉卡片           | **0**                 | **null**                       |
 *   | `am force-stop`             | **0**                 | **null**                       |
 *
 * 也就是说 MIUI 把「划掉卡片」当成了 force-stop，并且在 force-stop 时
 * 主动把这两个 secure setting 清空 —— 这不是 AOSP 的行为（AOSP 只解绑不清设置），
 * 是 MIUI 的安全策略。清空之后系统不会再自动绑回来，必须重新授权。
 *
 * ── 怎么办 ────────────────────────────────────────────────────
 * 普通应用无权写 secure settings，所以「自己把自己救回来」需要
 * `android.permission.WRITE_SECURE_SETTINGS`。它是 signature|privileged 级，
 * 安装时系统一律不授予，**只有 adb 能给它一次**：
 *
 *     adb shell pm grant com.local.qrjump android.permission.WRITE_SECURE_SETTINGS
 *
 * （manifest 里必须先声明，否则 pm grant 会报
 *   `Package ... has not requested permission ...`，22:35 实测过。）
 *
 * 给过一次之后，本类就能在被清空的**下一次启动时**把授权写回去：
 *   - 打开 App（DevicesActivity.onCreate）→ 检查并自愈
 *   - 每次真的要唤起设备页之前（arm 之前）→ 再检查并自愈
 *
 * 换句话说：划掉卡片的那一瞬间救不回来（进程已经死了，没有任何代码在跑），
 * 但**下一次你打开 App 或点设备的那一刻**它会自动补回来，
 * 不需要你再去系统设置里点一次。
 *
 * ── 边界 ──────────────────────────────────────────────────────
 * 只写两个 key，且是**追加**而不是覆盖：会先读出现有的服务列表，
 * 只有当本应用的服务不在里面时才追加，绝不碰别的无障碍服务。
 * 没有这个权限时，本类所有方法都是安全的空操作，App 行为与以前完全一致。
 */
object A11yGuard {

    private const val TAG_AREA = "A11y"

    /** 本应用的无障碍服务组件全名，写进 secure setting 的就是这个字符串。 */
    private fun component(context: Context): String =
        ComponentName(context, AutoUnlockService::class.java).flattenToString()

    /** 当前 App 是否拿到了那个「只有 adb 能给」的权限。 */
    fun canWrite(context: Context): Boolean =
        context.checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS) ==
            PackageManager.PERMISSION_GRANTED

    /**
     * 读系统设置里，本应用的无障碍服务到底在不在。
     *
     * 注意：这个方法**只有**在拿到 WRITE_SECURE_SETTINGS 之后才可靠 ——
     * 否则读 `Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES` 会抛异常或返回 null
     * （该 key 从 Android 4.2 起对普通应用受限，README 6.4 记过）。
     * 拿不到权限时回退到 AccessibilityManager 的运行时视图。
     */
    fun isEnabled(context: Context): Boolean {
        if (canWrite(context)) {
            val raw = runCatching {
                Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
            }.getOrNull()
            if (raw != null) {
                return raw.split(':').any { it.equals(component(context), ignoreCase = true) }
            }
        }
        // 回退：运行时的已启用列表
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            ?: return false
        val enabled = runCatching {
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        }.getOrNull().orEmpty()
        return enabled.any {
            val info = it.resolveInfo?.serviceInfo ?: return@any false
            info.packageName == context.packageName &&
                info.name == AutoUnlockService::class.java.name
        }
    }

    /**
     * 尝试把被系统清掉的授权写回去。
     *
     * @return true 表示调用后确认已启用（可能是本来就在、也可能是刚补回来）。
     *
     * ── 写序：两套 ROM 的顺序是相反的 ────────────────────────────
     * - MIUI（Redmi K40S / Android 14）：**先** `accessibility_enabled=1`，
     *   **再** 写 services。反过来系统会把前者重置回 0。
     * - ColorOS（realme RMX6699 / Android 17）：**先** 写 services，
     *   **再** `accessibility_enabled=1`。反过来同样被重置回 0。
     *
     * 2026-11-07 09:34 在 realme 上实测：按 MIUI 顺序写，`settings get
     * accessibility_enabled` 直接变回 0，服务也绑不上。
     *
     * 所以这里不猜平台，而是**两种顺序都试**：先用 MIUI 顺序写一遍并复核，
     * 没生效就改用 ColorOS 顺序再写一遍。写两次的代价可以忽略，
     * 比在两台机器上各维护一个分支可靠得多。
     */
    fun heal(context: Context): Boolean {
        if (!Prefs.autoUnlock(context)) {
            // 用户没开这个功能，就别去动系统的无障碍设置
            return false
        }
        if (!canWrite(context)) {
            QLog.w(
                TAG_AREA,
                "没有 WRITE_SECURE_SETTINGS，无法自愈。" +
                    "一次性执行：adb shell pm grant ${context.packageName} " +
                    "android.permission.WRITE_SECURE_SETTINGS"
            )
            return isEnabled(context)
        }
        if (isEnabled(context)) {
            QLog.i(TAG_AREA, "无障碍授权在位，无需自愈")
            return true
        }

        val mine = component(context)
        val cr = context.contentResolver

        // 读出现有列表，只做「追加」，绝不覆盖别人的服务
        val before = runCatching {
            Settings.Secure.getString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        }.getOrNull().orEmpty()

        val after = if (before.isBlank()) mine else "$before:$mine"

        QLog.w(TAG_AREA, "检测到无障碍授权被清空（多半是从最近任务划掉了 App），开始写回")
        QLog.i(TAG_AREA, "  原有服务列表=${before.ifBlank { "(空)" }}")
        QLog.i(TAG_AREA, "  写回后=$after")

        // 顺序 A（MIUI）：先 enabled，再 services
        writeAll(cr, enabledFirst = true, services = after)
        if (stillEnabled(cr, mine)) {
            QLog.i(TAG_AREA, "✓ 自愈成功（顺序 A：先 enabled 后 services）")
            return true
        }

        // 顺序 B（ColorOS）：先 services，再 enabled
        QLog.i(TAG_AREA, "顺序 A 没生效，改试顺序 B（先 services 后 enabled）")
        writeAll(cr, enabledFirst = false, services = after)
        val ok = stillEnabled(cr, mine)
        if (ok) {
            QLog.i(TAG_AREA, "✓ 自愈成功（顺序 B：先 services 后 enabled）")
        } else {
            QLog.e(
                TAG_AREA,
                "✗ 两种写序都没生效，系统拦截了第三方写入。" +
                    "需要你手动去系统设置里重新打开一次无障碍"
            )
        }
        return ok
    }

    /** 按指定顺序写两个 key。返回是否两次写入都无异常。 */
    private fun writeAll(
        cr: android.content.ContentResolver,
        enabledFirst: Boolean,
        services: String
    ): Boolean {
        val putEnabled = {
            runCatching {
                Settings.Secure.putInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 1)
            }.isSuccess
        }
        val putServices = {
            runCatching {
                Settings.Secure.putString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, services)
            }.isSuccess
        }
        val a: Boolean
        val b: Boolean
        if (enabledFirst) {
            a = putEnabled(); b = putServices()
        } else {
            a = putServices(); b = putEnabled()
        }
        QLog.i(TAG_AREA, "  write(enabledFirst=$enabledFirst) 两次写入结果=$a/$b")
        return a && b
    }

    /** 用系统视角复核：服务在列表里、且开关为 1。别只信写入的返回值。 */
    private fun stillEnabled(cr: android.content.ContentResolver, mine: String): Boolean {
        val services = runCatching {
            Settings.Secure.getString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        }.getOrNull()
        val flag = runCatching {
            Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 0)
        }.getOrDefault(0)
        val inList = services?.contains(mine, ignoreCase = true) == true
        if (!inList || flag != 1) {
            QLog.i(TAG_AREA, "  复核：服务在列表=$inList  开关=$flag")
        }
        return inList && flag == 1
    }

    /** 给设置页/日志用的一行状态描述。 */
    fun statusLine(context: Context): String {
        val enabled = isEnabled(context)
        val writable = canWrite(context)
        return when {
            enabled && writable -> "无障碍：已启用，且可自愈"
            enabled && !writable -> "无障碍：已启用，但不能自愈（划掉卡片后会掉，需重新授权）"
            !enabled && writable -> "无障碍：未启用，但有权限，下次唤起前会自动补上"
            else -> "无障碍：未启用，且无自愈权限"
        }
    }
}
