package com.local.qrjump

import java.net.URI
import java.net.URLDecoder

/**
 * 扫码结果的解析与严格校验。
 *
 * ── 为什么有「两种形态」──
 * 真机上遇到了两种机身码，它们指向同一个官方分发页，但参数完全不同：
 *
 *   形态①（饮水器）
 *     https://h5.qiekj.com/skip?SN=<你的15位SN>
 *     query = { SN }，值是 15 位纯数字
 *
 *   形态②（洗澡热水控制）
 *     https://link.wtturl.cn/?app_id=1128&...&target=<URL编码后的内层地址>
 *     解码 target 后 = https://h5.qiekj.com/skip?Company=qiekj&CommunicateType=1&Ver=4&NQT=<UUID>
 *     query = { Company, CommunicateType, Ver, NQT }，标识是 NQT（UUID）
 *
 * 所以校验不能只认 SN。改成分两层：
 *
 *   A. 「解包」——把码还原到 h5.qiekj.com/skip
 *      允许两种入口：直接是官方域名；或是指定的一家跳转包装域名（形态②的
 *      link.wtturl.cn），此时取它的 target 参数继续解，最多解 4 层。
 *      **包装层的 target 解出来之后仍然要过后面全部校验**，
 *      所以"套个壳就能让我打开任意地址"是不成立的。
 *
 *   B. 「校验」——只看解包后的内层地址
 *      1. scheme 恰为 https
 *      2. host 恰为 h5.qiekj.com（精确相等，不做后缀匹配，挡掉 h5.qiekj.com.evil.tld）
 *      3. path 恰为 /skip
 *      4. query 至少有一个参数，无重复 key，值里不含控制字符
 *      5. 必须含一个已知标识：SN（纯数字）或 NQT（UUID 形状）
 *
 * 刻意不做的事：
 *   - 不"纠正" http→https 之类的错误协议（宁可拒绝，也不猜用户意图）
 *   - 不跟随 HTTP 重定向（只看字符串，不发请求）
 *   - 不硬编码任何设备号，标识一律运行时从码里取
 *   - 不因为"看起来像我们的设备"就放行未知域名
 */
sealed class ParseResult {

    /**
     * 校验通过。
     *
     * @param launchUrl 给策略 A / C 用：真正要丢给浏览器或官方分发的地址。
     *                  形态②下它是**原始包装地址**（保持机上印的那串不动，
     *                  因为包装层可能带一次性参数，重拼反而可能失效）。
     * @param innerUrl  解包并规范化后的 h5.qiekj.com/skip 地址（一定是官方域名）。
     * @param id        设备标识的值：形态①是 15 位数字，形态②是 UUID 字符串。
     * @param idKind    标识种类，[ID_SN] 或 [ID_NQT]。
     * @param params    内层地址的全部 query 参数（有序），给策略 B 拼 query 用。
     */
    data class Ok(
        val launchUrl: String,
        val innerUrl: String,
        val id: String,
        val idKind: String,
        val params: LinkedHashMap<String, String>
    ) : ParseResult() {

        /** 形如 "SN=<你的15位SN>" 或 "Company=qiekj&...&NQT=..." 的可读串。 */
        fun paramsText(): String = params.entries.joinToString("&") { "${it.key}=${it.value}" }
    }

    /** 校验不通过，reason 是给日志和 UI 看的原因。 */
    data class Reject(val reason: String) : ParseResult()

    companion object {
        const val EXPECTED_HOST = "h5.qiekj.com"
        const val EXPECTED_PATH = "/skip"

        const val ID_SN = "SN"
        const val ID_NQT = "NQT"

        /**
         * 允许的「跳转包装」域名。
         *
         * 目前只有真机上实际观察到的 link.wtturl.cn（第三方短链/安全跳转服务，
         * 形态②的机身码就是它）。遇到新的一层包装时，把域名加进来即可，
         * 但**加进来不等于放行**：它的 target 解出来后仍要过全套校验。
         */
        val DELEGATE_HOSTS: Set<String> = setOf("link.wtturl.cn")

        /** 单条码内容上限。形态②的包装地址实测约 600 字符，留足余量。 */
        private const val MAX_TEXT = 4096

        /** 最多解几层包装，防构造环形跳转把解析拖死。 */
        private const val MAX_UNWRAP = 4

        /** 标识格式。SN：8~32 位纯数字；NQT：标准 UUID 形状。 */
        private val SN_PATTERN = Regex("^[0-9]{8,32}$")
        private val NQT_PATTERN = Regex(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-" +
                "[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
        )

        /** 参数值里不允许出现的控制字符。 */
        private val CONTROL_CHARS = Regex("[\\u0000-\\u001F\\u007F]")

        /** 参数值的长度上限，防超长串塞爆日志与 Intent。 */
        private const val MAX_VALUE_LEN = 256

        fun parse(rawText: String?): ParseResult {
            val text = rawText?.trim().orEmpty()
            if (text.isEmpty()) return ParseResult.Reject("扫码内容为空")
            if (text.length > MAX_TEXT) {
                return ParseResult.Reject("内容过长（${text.length} 字符），疑似非设备码")
            }

            // ── 第一步：解包，还原到官方内层地址 ──
            var current = text
            var depth = 0
            while (true) {
                if (depth > MAX_UNWRAP) {
                    return ParseResult.Reject("跳转包装超过 $MAX_UNWRAP 层，拒绝继续解")
                }

                val uri = try {
                    URI(current)
                } catch (e: Throwable) {
                    return ParseResult.Reject("不是合法 URI: ${QLog.describe(e)}")
                }

                // 1. scheme 必须在每一层都成立
                val scheme = uri.scheme
                if (scheme != "https") {
                    return ParseResult.Reject("scheme 不是 https（实际=${scheme ?: "null"}）")
                }

                val host = uri.host
                if (host.isNullOrEmpty()) {
                    return ParseResult.Reject("URL 没有 host")
                }

                if (host.equals(EXPECTED_HOST, ignoreCase = true)) {
                    // 到官方域名了，接下来做完整校验
                    return validateInner(uri, launchUrl = if (depth == 0) null else text, depth = depth)
                }

                if (DELEGATE_HOSTS.any { it.equals(host, ignoreCase = true) }) {
                    if (uri.path != null && uri.path != "/" && uri.path != "") {
                        return ParseResult.Reject("包装地址的 path 不是 /（实际=${uri.path}）")
                    }
                    val target = queryParam(uri.rawQuery, "target")
                        ?: return ParseResult.Reject("包装地址 $host 里没有 target 参数")
                    QLog.i("Parse", "解包第 ${depth + 1} 层（$host）→ $target")
                    current = target
                    depth++
                    continue
                }

                return ParseResult.Reject("host 不在允许列表（实际=$host，允许=$EXPECTED_HOST 或 ${DELEGATE_HOSTS.joinToString("/")}）")
            }
        }

        /**
         * 对已经落在官方域名上的内层地址做全部校验。
         *
         * @param launchUrl 非 null 表示这次是解包出来的（形态②），
         *                  A/C 档要用原始包装地址而不是重拼后的内层地址。
         */
        private fun validateInner(uri: URI, launchUrl: String?, depth: Int): ParseResult {
            // 3. path 精确相等
            val path = uri.path
            if (path != EXPECTED_PATH) {
                return ParseResult.Reject("path 不匹配（实际=${path ?: "null"}，期望=$EXPECTED_PATH）")
            }

            // 4. query 解析
            val rawQuery = uri.rawQuery
            if (rawQuery.isNullOrEmpty()) {
                return ParseResult.Reject("缺少 query")
            }

            val params = LinkedHashMap<String, String>()
            for (pair in rawQuery.split("&")) {
                if (pair.isEmpty()) continue
                val idx = pair.indexOf('=')
                if (idx < 0) {
                    return ParseResult.Reject("query 段缺少 '='：$pair")
                }
                val k: String
                val v: String
                try {
                    k = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                    v = URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                } catch (e: Throwable) {
                    return ParseResult.Reject("query 解码失败: ${QLog.describe(e)}")
                }
                if (k.isEmpty()) return ParseResult.Reject("query 里有空 key")
                if (params.containsKey(k)) {
                    return ParseResult.Reject("query 出现重复参数：$k")
                }
                if (v.length > MAX_VALUE_LEN) {
                    return ParseResult.Reject("参数 $k 过长（${v.length} 字符）")
                }
                if (CONTROL_CHARS.containsMatchIn(k) || CONTROL_CHARS.containsMatchIn(v)) {
                    return ParseResult.Reject("参数 $k 含控制字符")
                }
                params[k] = v
            }

            if (params.isEmpty()) return ParseResult.Reject("query 没有任何参数")

            // 5. 必须有一个已知标识
            val sn = params[ID_SN]
            val nqt = params[ID_NQT]

            val id: String
            val idKind: String
            when {
                sn != null && SN_PATTERN.matches(sn) -> {
                    id = sn
                    idKind = ID_SN
                }
                nqt != null && NQT_PATTERN.matches(nqt) -> {
                    id = nqt
                    idKind = ID_NQT
                }
                sn != null -> return ParseResult.Reject("SN 不是纯数字或长度异常（len=${sn.length}）")
                nqt != null -> return ParseResult.Reject("NQT 不是 UUID 形状（len=${nqt.length}）")
                else -> return ParseResult.Reject(
                    "query 里既没有 SN 也没有 NQT（实际参数=${params.keys}）"
                )
            }

            // 规范化内层地址：用校验过的 host/path/params 重拼，
            // 丢掉 fragment 与 host 大小写差异。
            val normalized = StringBuilder().apply {
                append("https://").append(EXPECTED_HOST).append(EXPECTED_PATH).append('?')
                var first = true
                for ((k, v) in params) {
                    if (!first) append('&')
                    first = false
                    append(k).append('=').append(v)
                }
            }.toString()

            if (depth > 0) {
                QLog.i("Parse", "已解包到官方内层地址（$depth 层包装）：$normalized")
            }

            // 形态②的 launchUrl 用原始包装串；形态①用规范化后的内层串
            return ParseResult.Ok(
                launchUrl = launchUrl ?: normalized,
                innerUrl = normalized,
                id = id,
                idKind = idKind,
                params = params
            )
        }

        /** 从 rawQuery 里取单个参数（已 URL 解码），取不到返回 null。 */
        private fun queryParam(rawQuery: String?, key: String): String? {
            if (rawQuery.isNullOrEmpty()) return null
            for (pair in rawQuery.split("&")) {
                if (pair.isEmpty()) continue
                val idx = pair.indexOf('=')
                if (idx < 0) continue
                val k = URLDecoder.decode(pair.substring(0, idx), "UTF-8")
                if (k == key) {
                    return URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
                }
            }
            return null
        }

        /**
         * 设置页预览/测试用：把设置里填的东西变成一个可用的 [Ok]。
         *
         * - 填的是整条 URL（含包装层）→ 走完整 [parse]
         * - 只填了裸标识        → 按形态①合成 `?SN=<值>`
         */
        fun fromUserInput(input: String): ParseResult {
            val t = input.trim()
            if (t.isEmpty()) return ParseResult.Reject("输入为空")
            if (t.startsWith("http://", true) || t.startsWith("https://", true)) {
                return parse(t)
            }
            if (!SN_PATTERN.matches(t) && !NQT_PATTERN.matches(t)) {
                return ParseResult.Reject("既不是 URL，也不是合法的 SN（纯数字）或 NQT（UUID）")
            }
            val params = LinkedHashMap<String, String>()
            val kind = if (SN_PATTERN.matches(t)) ID_SN else ID_NQT
            params[kind] = t
            val url = "$EXPECTED_HOST_PREFIX$EXPECTED_PATH?$kind=$t"
            return ParseResult.Ok(
                launchUrl = url,
                innerUrl = url,
                id = t,
                idKind = kind,
                params = params
            )
        }

        private const val EXPECTED_HOST_PREFIX = "https://h5.qiekj.com"
    }
}
