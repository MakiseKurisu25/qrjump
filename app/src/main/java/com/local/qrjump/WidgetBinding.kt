package com.local.qrjump

import android.content.Context
import org.json.JSONObject

/**
 * 「哪个桌面小部件 -> 哪台设备」的绑定表。
 *
 * ── 为什么需要单独一张表 ──
 * 改成「一台设备一个 1×1 方块」之后，桌面上可以同时存在多个实例：
 * 南五801 一个、洗澡一个。每个实例都是一个独立的小部件 id
 * （AppWidgetManager 分配），彼此之间唯一的区别就是「绑给谁」。
 * 这个 id 与设备的对应关系必须自己存 —— 系统不知道也不关心。
 *
 * ── 存的是什么 ──
 * 只存 `设备键`（形如 `SN|<你的15位SN>` / `NQT|9b9642e8-...`）：
 * 设备标识机身印着、公开可见，不含 token / 签名 / 登录态 / 订单。
 * 地址本身也不存 —— 点击时拿键回 [DeviceStore] 查，查到还要重新跑一遍
 * [ParseResult.parse]。所以即使这张表被改坏，也顶多指向一台"查不到的设备"，
 * 不会变成可以任意 URL 启动的后门。
 */
object WidgetBinding {

    /** JSON 对象：`{"widgetId": "设备键"}`。与其它设置同在一个偏好文件里。 */
    private const val KEY_BINDINGS = "widget_bindings"

    /**
     * 「放到桌面」时先记下这台设备，等系统拉起配置页就自动绑它、不再问一遍。
     * 手动从选择器拖出来的小部件没有这个值，配置页会正常弹选择框。
     */
    private const val KEY_PENDING = "widget_pending_pin"

    /** 只保留这轮还活着的小部件 id，别让绑定表无限膨胀。 */
    private const val MAX_BINDINGS = 32

    private fun sp(context: Context) =
        context.applicationContext.getSharedPreferences(Prefs.FILE, Context.MODE_PRIVATE)

    private fun load(context: Context): JSONObject {
        val raw = sp(context).getString(KEY_BINDINGS, null)
        if (raw.isNullOrBlank()) return JSONObject()
        return try {
            JSONObject(raw)
        } catch (t: Throwable) {
            QLog.w("Widget", "绑定表解析失败，按空处理：${QLog.describe(t)}")
            JSONObject()
        }
    }

    private fun save(context: Context, o: JSONObject) {
        sp(context).edit().putString(KEY_BINDINGS, o.toString()).apply()
    }

    /** 这个小部件绑的是哪台；没绑过返回 null。 */
    fun deviceKey(context: Context, widgetId: Int): String? {
        val v = load(context).optString(widgetId.toString(), "")
        return if (v.isBlank()) null else v
    }

    fun bind(context: Context, widgetId: Int, deviceKey: String) {
        val o = load(context)
        o.put(widgetId.toString(), deviceKey)
        if (o.length() > MAX_BINDINGS) {
            // 顶多 32 条，正常用不到；真到了就整表重建，反正下一轮 onUpdate 会补齐。
            val trimmed = JSONObject()
            o.keys().asSequence().toList().takeLast(MAX_BINDINGS).forEach { k ->
                trimmed.put(k, o.optString(k, ""))
            }
            save(context, trimmed)
        } else {
            save(context, o)
        }
        QLog.i("Widget", "小部件 $widgetId 绑定到 $deviceKey")
    }

    fun unbind(context: Context, widgetId: Int) {
        val o = load(context)
        if (!o.has(widgetId.toString())) return
        o.remove(widgetId.toString())
        save(context, o)
        QLog.i("Widget", "小部件 $widgetId 的绑定已删除")
    }

    /**
     * 清理已经不存在的实例。
     *
     * 用户从桌面拖掉一个方块时系统不会通知得很及时（onDeleted 有时收不到），
     * 所以每次刷新顺便按「当前活着的 id 集合」清一遍，避免表里留垃圾。
     */
    fun prune(context: Context, alive: Set<Int>) {
        val o = load(context)
        if (o.length() == 0) return
        val dead = o.keys().asSequence().filter { k ->
            val id = k.toIntOrNull()
            id == null || id !in alive
        }.toList()
        if (dead.isEmpty()) return
        dead.forEach { o.remove(it) }
        save(context, o)
        QLog.i("Widget", "清理了 ${dead.size} 条已失效的小部件绑定")
    }

    // ------------------------------------------------------------ 待绑定

    fun setPendingPin(context: Context, deviceKey: String) {
        sp(context).edit().putString(KEY_PENDING, deviceKey).apply()
    }

    /** 取出并清空（一次有效，避免下次手动添加小部件时被这次的值误导）。 */
    fun takePendingPin(context: Context): String? {
        val v = sp(context).getString(KEY_PENDING, null)
        if (v.isNullOrBlank()) return null
        sp(context).edit().remove(KEY_PENDING).apply()
        return v
    }
}
