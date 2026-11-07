package com.local.qrjump

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * 设备簿：记住扫过的每一台机器，供「设备列表」自由选择。
 *
 * ── 这里存的是什么 ──
 * 只存**通过完整校验的设备码原文**，以及从里面解出来的设备标识（SN / NQT）
 * 和用户自己起的备注名。设备标识机身印着、公开可见，不含任何 token /
 * 签名 / 登录态 / 订单信息。所以记住它，本质上是「本 App 认得这台机器」，
 * 不涉及鉴权，也不代替官方身份校验。
 *
 * ── 为什么每次读出来还要重新校验 ──
 * [all] 会把每条存下来的地址重新丢给 [ParseResult.parse] 走完整校验，
 * 而不是直接信任 SharedPreferences 里的内容。这样即使偏好文件被改坏、
 * 或被别的工具写入奇怪的值，也不会绕过 host / path / 标识格式的检查。
 * 校验不过的条目会被跳过并记日志，不会成为"信任边界之外的后门"。
 *
 * ── 不做的事 ──
 * 不缓存任何网络响应，不缓存小程序页面，不保存支付结果、用水量或余额。
 * 只记"这台机器是哪台"。
 */
object DeviceStore {

    /** 最多记这么多台，防止无限增长。超出时丢掉最久没用的。 */
    const val MAX_DEVICES = 20

    /**
     * 一台设备。
     *
     * @param launchUrl 通过校验的完整地址（形态②下是原始包装地址）
     * @param innerUrl  解包后的内层官方地址
     * @param id        设备标识（SN 的 15 位数字 / NQT 的 UUID）
     * @param idKind    [ParseResult.ID_SN] 或 [ParseResult.ID_NQT]
     * @param params    内层地址的 query 原文
     * @param time      上次使用的毫秒时间戳，仅用于显示排序
     * @param alias     用户自己起的名字，空 = 用标识当名字
     */
    data class Device(
        val launchUrl: String,
        val innerUrl: String,
        val id: String,
        val idKind: String,
        val params: String,
        val time: Long,
        val alias: String
    ) {
        /** 去重/查找用的稳定键。 */
        val key: String get() = "$idKind|$id"
    }

    private fun sp(context: Context) =
        context.applicationContext.getSharedPreferences(Prefs.FILE, Context.MODE_PRIVATE)

    // ---------------------------------------------------------------- 读

    /**
     * 全部设备，最近用过的排在最前。
     *
     * 每条都会重新走一遍 [ParseResult.parse]：校验不过的直接丢弃（并在日志里说明），
     * 所以记忆槽不是"信任边界之外的后门"。
     */
    fun all(context: Context): List<Device> {
        val raw = sp(context).getString(Prefs.KEY_DEVICES, null)
        if (raw.isNullOrBlank()) return migrateLegacy(context)

        val arr = try {
            JSONArray(raw)
        } catch (t: Throwable) {
            QLog.w("Memory", "设备簿 JSON 解析失败，按空处理：${QLog.describe(t)}")
            return emptyList()
        }

        val out = ArrayList<Device>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            val url = o.optString("url", "")
            if (url.isBlank()) continue

            val parsed = ParseResult.parse(url)
            if (parsed !is ParseResult.Ok) {
                QLog.w(
                    "Memory",
                    "设备簿第 ${i + 1} 条已不再通过校验，跳过：" +
                        (parsed as? ParseResult.Reject)?.reason
                )
                continue
            }
            out.add(
                Device(
                    launchUrl = parsed.launchUrl,
                    innerUrl = parsed.innerUrl,
                    id = parsed.id,
                    idKind = parsed.idKind,
                    params = parsed.paramsText(),
                    time = o.optLong("time", 0L),
                    alias = o.optString("alias", "")
                )
            )
        }
        out.sortByDescending { it.time }
        return out
    }

    /** 按 key 取一台；不存在返回 null。 */
    fun get(context: Context, idKind: String, id: String): Device? =
        all(context).firstOrNull { it.idKind == idKind && it.id == id }

    // ---------------------------------------------------------------- 写

    /**
     * 记住一次成功解析的设备码。已存在同一个标识时只更新时间（保留备注名），
     * 并把它移到最前。
     */
    fun remember(context: Context, ok: ParseResult.Ok) {
        val now = System.currentTimeMillis()
        val list = all(context).toMutableList()

        val idx = list.indexOfFirst { it.idKind == ok.idKind && it.id == ok.id }
        val alias = if (idx >= 0) list[idx].alias else ""

        val d = Device(
            launchUrl = ok.launchUrl,
            innerUrl = ok.innerUrl,
            id = ok.id,
            idKind = ok.idKind,
            params = ok.paramsText(),
            time = now,
            alias = alias
        )
        if (idx >= 0) list.removeAt(idx)
        list.add(0, d)

        val capped = if (list.size > MAX_DEVICES) list.subList(0, MAX_DEVICES).toList() else list
        write(context, capped)

        QLog.i(
            "Memory",
            "已记住设备：形态=${ok.idKind} 标识=${ok.id}（${if (idx >= 0) "已存在，更新时间" else "新设备"}，" +
                "当前共 ${capped.size} 台）"
        )
    }

    /** 改备注名。传空字符串等于取消备注。 */
    fun rename(context: Context, key: String, alias: String) {
        val list = all(context).toMutableList()
        val idx = list.indexOfFirst { it.key == key }
        if (idx < 0) {
            QLog.w("Memory", "改名前没找到这台设备：$key")
            return
        }
        list[idx] = list[idx].copy(alias = alias.trim())
        write(context, list)
        QLog.i("Memory", "设备改名：$key → 「${alias.trim()}」")
    }

    /** 删掉一台。 */
    fun forget(context: Context, key: String) {
        val list = all(context).filterNot { it.key == key }
        write(context, list)
        QLog.i("Memory", "已删除设备：$key（剩余 ${list.size} 台）")
    }

    /** 清空设备簿。 */
    fun clear(context: Context) {
        sp(context).edit()
            .remove(Prefs.KEY_DEVICES)
            .remove(Prefs.KEY_LAST_URL)
            .remove(Prefs.KEY_LAST_INNER)
            .remove(Prefs.KEY_LAST_ID)
            .remove(Prefs.KEY_LAST_ID_KIND)
            .remove(Prefs.KEY_LAST_PARAMS)
            .remove(Prefs.KEY_LAST_TIME)
            .apply()
        // 设备簿清空后，桌面上的快捷方式也必须一起清掉，否则会留下点了没反应的僵尸入口。
        ShortcutHelper.sync(context)
        DeviceWidgetProvider.refresh(context)
        QLog.i("Memory", "已清空设备簿")
    }

    // ---------------------------------------------------------------- 内部

    private fun write(context: Context, list: List<Device>) {
        val arr = JSONArray()
        for (d in list) {
            val o = JSONObject()
            o.put("url", d.launchUrl)
            o.put("inner", d.innerUrl)
            o.put("id", d.id)
            o.put("kind", d.idKind)
            o.put("params", d.params)
            o.put("time", d.time)
            o.put("alias", d.alias)
            arr.put(o)
        }
        sp(context).edit().putString(Prefs.KEY_DEVICES, arr.toString()).apply()

        // 设备簿一变，桌面快捷方式跟着变，避免留下去不掉的僵尸入口。
        // sync 内部会重新 all() 一次，读的是刚写进去的内容。
        ShortcutHelper.sync(context)
        // 桌面小部件同理：设备簿变了，桌面那块也要立刻反映（否则要等下次开机）。
        DeviceWidgetProvider.refresh(context)
    }

    /**
     * 旧版本只有"一台"的记忆槽，迁移成列表。
     *
     * 迁移后保留旧键不删，避免迁移过程中出意外把唯一的数据弄丢；
     * 下一轮 remember 写入新键后它们自然不再被读。
     */
    private fun migrateLegacy(context: Context): List<Device> {
        val url = sp(context).getString(Prefs.KEY_LAST_URL, null)
        if (url.isNullOrBlank()) return emptyList()

        val parsed = ParseResult.parse(url)
        if (parsed !is ParseResult.Ok) {
            QLog.w("Memory", "旧记忆槽内容已不再通过校验，不迁移")
            return emptyList()
        }
        val d = Device(
            launchUrl = parsed.launchUrl,
            innerUrl = parsed.innerUrl,
            id = parsed.id,
            idKind = parsed.idKind,
            params = parsed.paramsText(),
            time = sp(context).getLong(Prefs.KEY_LAST_TIME, 0L),
            alias = ""
        )
        write(context, listOf(d))
        QLog.i("Memory", "旧版单条记忆已迁移为设备簿：形态=${d.idKind} 标识=${d.id}")
        return listOf(d)
    }
}
