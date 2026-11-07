package com.local.qrjump

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

/**
 * 桌面小部件：**一台设备一个 1×1 方块**。
 *
 * ── 为什么是小部件 ──
 * 「放到桌面」原本靠两条路，在这台 MIUI 机器上**都不通**（2026-11-06 实测）：
 *   1. `requestPinShortcut` 返回 true 却什么都不画（MIUI 屏蔽了展示层）；
 *   2. 旧版 `INSTALL_SHORTCUT` 广播被系统直接丢弃，logcat 原文：
 *      `W/ActivityManager: Broadcast com.android.launcher.action.INSTALL_SHORTCUT
 *       no longer supported. It will not be delivered.`
 * 而桌面小部件是 Android 从 1.5 就有、从未被废弃、MIUI 也完整支持的机制。
 *
 * ── 从「一块列表」改成「一格一台」的理由 ──
 * 列表版是 3×2 的一大块，桌面上一眼看去是个面板，点之前还要在面板里找行。
 * 1×1 是一格一个方块，位置由用户自己摆，直觉上就等于"这台机器"：
 * 南五801 放左边、洗澡放右边，按位置记比按文字记快。
 *
 * ── 每个实例怎么知道自己是哪台 ──
 * 系统给每个小部件分配一个独立的 id，但**不会**告诉你它绑给谁。
 * 所以对应关系存在 [WidgetBinding] 里（id -> 设备键）。两条入口：
 *   · 从 App 里点「放到桌面」：先记一次 pending，系统拉起 [WidgetConfigActivity]
 *     后它直接把 pending 绑上去，用户不用再选一次；
 *   · 手动从选择器拖：没有 pending，[WidgetConfigActivity] 会正常弹一个选择框。
 *
 * ── 安全边界 ──
 * 小部件只携带设备键（`SN|8622...`），点击后交给 [ShortcutActivity]，
 * 由它回设备簿查询并**重新跑一遍完整校验**。小部件本身不存 URL、不存 token，
 * 也不会因为没有绑定就退化成"打开某个固定网址"。
 */
class DeviceWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        QLog.i("Widget", "收到刷新：${appWidgetIds.size} 个实例")
        appWidgetIds.forEach { id ->
            // 有些机型（含 MIUI）在"钉到桌面"成功后会先发一次 UPDATE 再拉配置页，
            // 这时 pending 还在：先绑上，用户等会儿就不必再选一遍。
            if (WidgetBinding.deviceKey(context, id) == null) {
                val pending = WidgetBinding.takePendingPin(context)
                if (pending != null) {
                    QLog.i("Widget", "实例 $id 命中待绑定设备 $pending")
                    WidgetBinding.bind(context, id, pending)
                }
            }
            appWidgetManager.updateAppWidget(id, buildViews(context, id))
        }
        WidgetBinding.prune(
            context,
            appWidgetManager.getAppWidgetIds(ComponentName(context, DeviceWidgetProvider::class.java)).toSet()
        )
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { WidgetBinding.unbind(context, it) }
        QLog.i("Widget", "用户移除 ${appWidgetIds.size} 个实例")
    }

    override fun onEnabled(context: Context) {
        QLog.i("Widget", "第一个实例被添加")
    }

    override fun onDisabled(context: Context) {
        QLog.i("Widget", "最后一个实例被移除")
    }

    companion object {

        /**
         * 设备簿一变就调它（和 [ShortcutHelper.sync] 并列调用）。
         *
         * 绑定的设备被删掉时，这里会把它显示成「已失效，点一下重新选择」，
         * 而不是留一个点了没反应的方块。
         */
        fun refresh(context: Context) {
            val mgr = AppWidgetManager.getInstance(context) ?: return
            val cn = ComponentName(context, DeviceWidgetProvider::class.java)
            val ids = try {
                mgr.getAppWidgetIds(cn)
            } catch (t: Throwable) {
                QLog.w("Widget", "取小部件实例失败：${QLog.describe(t)}")
                return
            }
            if (ids.isEmpty()) return
            QLog.i("Widget", "设备簿变化，刷新 ${ids.size} 个实例")
            ids.forEach { id -> mgr.updateAppWidget(id, buildViews(context, id)) }
            WidgetBinding.prune(context, ids.toSet())
        }

        /** 某个实例换绑之后立刻重画它自己。 */
        fun updateOne(context: Context, widgetId: Int) {
            val mgr = AppWidgetManager.getInstance(context) ?: return
            mgr.updateAppWidget(widgetId, buildViews(context, widgetId))
        }

        private fun buildViews(context: Context, widgetId: Int): RemoteViews {
            val views = RemoteViews(context.packageName, R.layout.widget_cell)
            val key = WidgetBinding.deviceKey(context, widgetId)
            val d = key?.let { k -> DeviceStore.all(context).firstOrNull { it.key == k } }

            if (d == null) {
                // 没绑 / 绑的设备已被删：显示成一个可点的"空位"，点它去重新选。
                // 不显示成空白方块 —— 那样用户会以为小部件坏了。
                views.setImageViewResource(R.id.widgetCellIcon, R.drawable.ic_widget_water)
                views.setTextViewText(
                    R.id.widgetCellName,
                    context.getString(if (key == null) R.string.widget_cell_unbound else R.string.widget_cell_gone)
                )
                views.setOnClickPendingIntent(R.id.widgetCellRoot, configPending(context, widgetId))
                return views
            }

            views.setImageViewResource(
                R.id.widgetCellIcon,
                if (d.idKind == ParseResult.ID_SN) R.drawable.ic_widget_water else R.drawable.ic_widget_heat
            )
            views.setTextViewText(R.id.widgetCellName, nameOf(d))
            views.setOnClickPendingIntent(R.id.widgetCellRoot, launchPending(context, d))
            return views
        }

        private fun launchPending(context: Context, d: DeviceStore.Device): PendingIntent {
            val intent = Intent(context, ShortcutActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra(ShortcutActivity.EXTRA_KEY, d.key)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            // requestCode 用设备键的哈希，保证不同方块之间不互相覆盖
            // （RemoteViews 上最常见的坑：不给不同 requestCode 就全被当成同一个）。
            return PendingIntent.getActivity(
                context,
                d.key.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun configPending(context: Context, widgetId: Int): PendingIntent {
            val intent = Intent(context, WidgetConfigActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            return PendingIntent.getActivity(
                context,
                widgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        /**
         * 方块上那一行字。
         *
         * 只有一格宽，15 位设备号放不下，所以优先用用户自己起的备注名；
         * 没起名就退成「饮水器 031408」这种能认得出来的短名。
         */
        private fun nameOf(d: DeviceStore.Device): String =
            if (d.alias.isNotBlank()) {
                d.alias
            } else if (d.idKind == ParseResult.ID_SN) {
                "饮水器 …" + d.id.takeLast(6)
            } else {
                "热水 …" + d.id.take(4)
            }
    }
}
