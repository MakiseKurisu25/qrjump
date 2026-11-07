package com.local.qrjump

import android.content.Context
import android.content.SharedPreferences

/**
 * 设置项集中管理。全部用 SharedPreferences，不引入 DataStore，减少构建面。
 *
 * ── 默认策略为什么是 B 而不是 A ──
 * 真机实测：用 ACTION_VIEW 打开 https://h5.qiekj.com/skip?SN=...，
 * Edge 落到的是「胖乖生活 App 下载引导页」，既不是设备页也不提示用支付宝打开。
 * 而 B 档（alipays:// 直连）实测能一步进到官方小程序设备页。所以默认 B。
 * 但注意：B 的这个结论**只在形态①（SN 参数）的设备上验证过**。
 *
 * ── 两种设备形态 ──
 * 形态①饮水器：  .../skip?SN=<15位数字>
 * 形态②热水控制：.../skip?Company=qiekj&CommunicateType=1&Ver=4&NQT=<UUID>
 * 形态②在机上还多套了一层 link.wtturl.cn 的包装。
 * 形态②下 B 档的 query 曾按「直传机身码里那 4 个参数」拼，实测会被支付宝判
 * 「二维码有误」；2026-11-06 20:09 起改为与形态①同一套外层参数、只换标识键名
 * （masterSwitch=0&keepUsing=1&NQT=<UUID>&isShowAd=false），实测同样直达设备页。
 * 所以现在两种形态都是 B 优先、C 兜底。
 */
object Prefs {

    /** 偏好文件名。注意：[DeviceStore] 也用同一个文件，所以这里不能是 private。 */
    const val FILE = "qrjump_prefs"

    const val KEY_STRATEGY = "strategy"                 // "A" / "B" / "C"
    const val KEY_DEBUG_MODE = "debug_mode"             // Boolean，默认 false
    const val KEY_AUTO_FALLBACK = "auto_fallback"       // Boolean，默认 true
    const val KEY_AUTO_UNLOCK = "auto_unlock"           // Boolean，默认 false（自动点「立即使用」）
    const val KEY_AUTO_UNLOCK_DELAY = "auto_unlock_delay" // Int，**秒**，默认 8
    /** 认出设备页后等多久才点：默认 8 秒，允许 0–30 秒。 */
    const val DEFAULT_AUTO_UNLOCK_DELAY_SEC = 8
    const val MAX_AUTO_UNLOCK_DELAY_SEC = 30
    const val KEY_A11Y_CONNECTED = "a11y_connected"      // Boolean，无障碍服务连上时写的回执
    const val KEY_A11Y_PID = "a11y_pid"                  // Int，写回执时的进程号（换进程即作废）
    // 策略 B 的参数。留空则该档判为不可用，自动降级。
    const val KEY_ALIPAY_APP_ID = "alipay_app_id"       // 小程序 appId，留空=未配置
    const val KEY_ALIPAY_PAGE = "alipay_page"           // 小程序页面路径，留空=未配置
    const val KEY_ALIPAY_QUERY = "alipay_query_tpl"     // 形态①的 query 模板，含 {SN}
    const val KEY_ALIPAY_QUERY_NQT = "alipay_query_nqt" // 形态②的 query 模板，含 {NQT}

    /**
     * 下面三个默认值不是猜的，是**观察到的**：
     * 用策略 C（内置 WebView + 支付宝 UA）打开原始 URL 时，官方分发页自己会发起一次
     *   alipays://platformapi/startapp?appId=...&page=...&query=...
     * 跳转，我们只是把这个「官方页面自己产生的 Intent」原样打印到 logcat 并接手执行。
     * 没有抓包、没有逆向接口、没有任何伪造参数 —— 参数全部由官方页面给出。
     *
     * 即便如此，它们仍只是「默认预填值」：设置页可以随时清空或改写；
     * 清空 appId/page 后策略 B 自动判为不可用，退化成只用 A/C。
     */
    const val DEFAULT_APP_ID = "2018072460764274"
    const val DEFAULT_PAGE = "pages/skipNew/skipNew?isShowAd=false"

    /**
     * 形态①（SN）的 query 模板。{SN} 会被替换成运行时解析出来的数字设备号。
     *
     * 实测教训：只带 SN（query=SN%3Dxxx）会被小程序判「二维码有误，换一个试试」；
     * 必须连 masterSwitch / keepUsing 一起带上才能直达设备页。
     * 所以这里保留官方页面给出的完整形状，而不是精简成单个参数。
     */
    const val DEFAULT_QUERY_TEMPLATE = "masterSwitch=0&keepUsing=1&SN={SN}&isShowAd=false"

    /**
     * 形态②（NQT）的 query 模板。{NQT} 会被替换成运行时解析出来的设备 UUID。
     *
     * 实测教训（2026-11-06 20:07）：把机身码里官方那组
     *   Company=qiekj&CommunicateType=1&Ver=4&NQT=<UUID>
     * 原样直传，小程序落的是「读取设备 → 二维码有误，换一个试试」。
     * 换成和形态①同形状、只把 SN 换成 NQT 的模板后
     *   masterSwitch=0&keepUsing=1&NQT=<UUID>&isShowAd=false
     * 直接落到热水设备页（某热水机 201 / XX学院 / 小票余额 46.66 元）。
     * 所以两个形态共用同一套外层参数，区别只在标识键名。
     */
    const val DEFAULT_QUERY_TEMPLATE_NQT = "masterSwitch=0&keepUsing=1&NQT={NQT}&isShowAd=false"

    // ── 「扫过一次，以后一键直达」用的记忆槽 ──
    // 存的是上一次通过校验的设备码解析结果。它只是"我记得这台机器是哪台"，
    // 不含任何 token / 签名 / 登录态，也不代替官方鉴权。
    const val KEY_LAST_URL = "last_launch_url"
    const val KEY_LAST_INNER = "last_inner_url"
    const val KEY_LAST_ID = "last_id"
    const val KEY_LAST_ID_KIND = "last_id_kind"
    const val KEY_LAST_PARAMS = "last_params"
    const val KEY_LAST_TIME = "last_time"

    /**
     * 设备簿（多台）的存储键。里面是一个 JSON 数组，每项含
     * url / inner / id / kind / params / time / alias。
     *
     * 与上面的 KEY_LAST_* 并存：旧版只有单条记忆，[DeviceStore] 首次读不到
     * 数组时会用旧键迁移出一条，保证升级后"上次那台"不会丢。
     */
    const val KEY_DEVICES = "devices"

    const val KEY_ALIPAY_UA = "alipay_ua"               // 策略 C 是否伪装支付宝 UA
    const val KEY_ALIPAY_UA_VALUE = "alipay_ua_value"   // 自定义 UA 文本
    const val KEY_WEBVIEW_AUTO_SCHEME = "webview_auto_scheme"

    /**
     * 支付宝 Android WebView 的典型 UA 片段。
     * 注意：这只是"让 H5 认出请求来自支付宝"的常规手段，
     * 不携带任何身份、token 或登录态，也不绕过任何鉴权。
     */
    val DEFAULT_ALIPAY_UA: String = buildString {
        append("Mozilla/5.0 (Linux; U; Android 14; zh-CN; ")
        append(android.os.Build.MODEL.replace(' ', '_'))
        append(" Build/UP1A.231005.007) AppleWebKit/537.36 (KHTML, like Gecko) ")
        append("Version/4.0 Chrome/69.0.3497.100 Mobile Safari/537.36 ")
        append("AliApp(AP/10.5.96.8000) AlipayDefined(nt:WIFI,ws:412|915|2.75) ")
        append("AlipayClient/10.5.96.8000 Language/zh-Hans Region/CN")
    }

    private fun sp(c: Context): SharedPreferences =
        c.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun strategy(c: Context): String = sp(c).getString(KEY_STRATEGY, "B") ?: "B"
    fun setStrategy(c: Context, v: String) = sp(c).edit().putString(KEY_STRATEGY, v).apply()

    fun debugMode(c: Context): Boolean = sp(c).getBoolean(KEY_DEBUG_MODE, false)
    fun setDebugMode(c: Context, v: Boolean) = sp(c).edit().putBoolean(KEY_DEBUG_MODE, v).apply()

    fun autoFallback(c: Context): Boolean = sp(c).getBoolean(KEY_AUTO_FALLBACK, true)
    fun setAutoFallback(c: Context, v: Boolean) = sp(c).edit().putBoolean(KEY_AUTO_FALLBACK, v).apply()

    // appId / page 留空 -> 该档不可用；未写过时用实测默认值预填（可在设置页改）
    fun alipayAppId(c: Context): String =
        (sp(c).getString(KEY_ALIPAY_APP_ID, DEFAULT_APP_ID) ?: "").trim()

    fun setAlipayAppId(c: Context, v: String) = sp(c).edit().putString(KEY_ALIPAY_APP_ID, v.trim()).apply()

    fun alipayPage(c: Context): String =
        (sp(c).getString(KEY_ALIPAY_PAGE, DEFAULT_PAGE) ?: "").trim()

    fun setAlipayPage(c: Context, v: String) = sp(c).edit().putString(KEY_ALIPAY_PAGE, v.trim()).apply()

    fun alipayQueryTemplate(c: Context): String =
        (sp(c).getString(KEY_ALIPAY_QUERY, DEFAULT_QUERY_TEMPLATE) ?: "").trim()

    fun setAlipayQueryTemplate(c: Context, v: String) =
        sp(c).edit().putString(KEY_ALIPAY_QUERY, v.trim()).apply()

    fun alipayQueryTemplateNqt(c: Context): String =
        (sp(c).getString(KEY_ALIPAY_QUERY_NQT, DEFAULT_QUERY_TEMPLATE_NQT) ?: "").trim()

    fun setAlipayQueryTemplateNqt(c: Context, v: String) =
        sp(c).edit().putString(KEY_ALIPAY_QUERY_NQT, v.trim()).apply()

    /**
     * 策略 C 的 UA 伪装，默认开启。
     *
     * 原因是实测出来的：WebView 用默认 UA 打开 /skip 只落到 App 下载引导页；
     * 换上支付宝 UA 后，那个页面才会自己发起 alipays:// 跳转，C 档才有效。
     * 不开启伪装时 C 档基本等于 A 档，没有存在意义。
     */
    fun useAlipayUa(c: Context): Boolean = sp(c).getBoolean(KEY_ALIPAY_UA, true)
    fun setUseAlipayUa(c: Context, v: Boolean) = sp(c).edit().putBoolean(KEY_ALIPAY_UA, v).apply()



    /**
     * 自动点击「立即使用」。**默认关闭**。
     *
     * 关闭时，无障碍服务即使被系统启动也不会动手（AutoUnlock.arm 直接 return），
     * 保证"默认关闭"是彻底的关闭，而不只是界面上的一个勾。
     */
    fun autoUnlock(c: Context): Boolean = sp(c).getBoolean(KEY_AUTO_UNLOCK, false)
    fun setAutoUnlock(c: Context, v: Boolean) = sp(c).edit().putBoolean(KEY_AUTO_UNLOCK, v).apply()

    /**
     * 自动点击前，认出设备页之后再等多久（**秒**）。默认 **8 秒**。
     *
     * ── 为什么要等 ──
     * 2026-11-06 21:04 的 dump 证明：页面还在显示「内容准备中」的时候，
     * 「立即使用」这个节点**已经出现在控件树里了**。如果一看到它就点，
     * 点的是一个还没接上设备状态的页面，官方那边很可能不理、或者报错。
     * 用户实际体感也是「打开网页后加载有延迟」。
     *
     * ── 为什么单位是秒（2026-11-06 22:05 改）──
     * 之前的单位是毫秒、默认 2500，界面上是可读性很差的"2500 毫秒"，
     * 而且当时是两道闸相加（8 秒宽限 + 2.5 秒稳定 = 恒定 10.5 秒既慢又不可预测）。
     * 现在改成一条时钟：**认出设备页起等满这么多秒就点**，可预测、可调。
     * 默认从 2.5 秒提到 8 秒，是按用户「打开速度太慢、大胆往后调」的要求放的，
     * 覆盖楼里信号差、小程序冷启动慢的情况。设置页可调 0–30 秒。
     */
    fun autoUnlockDelaySec(c: Context): Int {
        val raw = sp(c).getInt(KEY_AUTO_UNLOCK_DELAY, DEFAULT_AUTO_UNLOCK_DELAY_SEC)
        // 旧版存的是毫秒（默认 2500）。秒的上限是 30，任何大于它的存量值
        // 都只可能是旧单位，就地换算一次并存回去，避免读出"2500 秒"。
        if (raw > MAX_AUTO_UNLOCK_DELAY_SEC) {
            val sec = (raw / 1000).coerceIn(0, MAX_AUTO_UNLOCK_DELAY_SEC)
            sp(c).edit().putInt(KEY_AUTO_UNLOCK_DELAY, sec).apply()
            return sec
        }
        return raw.coerceIn(0, MAX_AUTO_UNLOCK_DELAY_SEC)
    }

    fun setAutoUnlockDelaySec(c: Context, v: Int) =
        sp(c).edit().putInt(KEY_AUTO_UNLOCK_DELAY, v.coerceIn(0, MAX_AUTO_UNLOCK_DELAY_SEC)).apply()

    /**
     * 无障碍服务是否真的被系统绑上过。
     *
     * 由 [AutoUnlockService.onServiceConnected] 写 true、
     * [AutoUnlockService.onUnbind] 写 false。
     * 这比读 Settings.Secure 可靠：那个 key 普通应用读出来是 null。
     *
     * ── 回执必须绑进程号（2026-11-06 22:55 实测到的缺陷）──────────
     * 一开始只存了一个 Boolean。结果 `am force-stop` 之后授权被系统清空、
     * 服务也死了，但**进程被杀时不会回调 onUnbind**，那个 true 就留在盘上了，
     * 于是设备列表页明明没授权，日志还打 `无障碍服务=true`。
     * 现在连 `android.os.Process.myPid()` 一起存：进程重开之后 pid 变了，
     * 旧回执自动作废，不需要任何代码去清理它。
     */
    fun a11yConnected(c: Context): Boolean =
        sp(c).getBoolean(KEY_A11Y_CONNECTED, false) &&
            sp(c).getInt(KEY_A11Y_PID, -1) == android.os.Process.myPid()

    fun setA11yConnected(c: Context, v: Boolean) =
        sp(c).edit()
            .putBoolean(KEY_A11Y_CONNECTED, v)
            .putInt(KEY_A11Y_PID, if (v) android.os.Process.myPid() else -1)
            .apply()

    fun webviewAutoScheme(c: Context): Boolean = sp(c).getBoolean(KEY_WEBVIEW_AUTO_SCHEME, true)
    fun setWebviewAutoScheme(c: Context, v: Boolean) =
        sp(c).edit().putBoolean(KEY_WEBVIEW_AUTO_SCHEME, v).apply()

    fun customAlipayUa(c: Context): String {
        val v = (sp(c).getString(KEY_ALIPAY_UA_VALUE, "") ?: "").trim()
        return if (v.isEmpty()) DEFAULT_ALIPAY_UA else v
    }

    fun setCustomAlipayUa(c: Context, v: String) =
        sp(c).edit().putString(KEY_ALIPAY_UA_VALUE, v.trim()).apply()
}
