package com.local.qrjump

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * 一次唤起尝试的结果。策略之间靠这个类型传递，便于日志与 UI 如实展示。
 */
sealed class AttemptResult {
    /** 已经真正发起了 Intent（或 WebView Activity）。 */
    data class Started(
        val strategy: String,
        val intent: Intent?,
        val resolvedComponent: String?,
        val extra: String = ""
    ) : AttemptResult()

    /** 调试模式：只算出来要做什么，没有真正唤起。 */
    data class PlannedDryRun(
        val strategy: String,
        val intent: Intent?,
        val resolvedComponent: String?,
        val extra: String = ""
    ) : AttemptResult()

    /** 这一档在当前配置下压根不可用（例如 B 没填 appId），跳到下一档。 */
    data class Unavailable(val strategy: String, val reason: String) : AttemptResult()

    /** 尝试了但失败了，reason 说明失败点。 */
    data class Failed(val strategy: String, val reason: String) : AttemptResult()
}

/**
 * 三档唤起策略的实现与降级编排。
 *
 * 红线在这里体现为：
 *   - 只构造"打开某个官方页面/官方小程序"的 Intent
 *   - 不构造任何支付、下单、开机、出水的指令
 *   - 不读取、不篡改、不伪造任何 token / 签名 / 订单号
 *   - 设备标识只作为 URL query 里的普通参数传递，和机身二维码里带的一模一样
 */
object Launcher {

    const val STRATEGY_A = "A"   // ACTION_VIEW 直接打开原始 URL
    const val STRATEGY_B = "B"   // 经支付宝 scheme / ds.alipay.com 唤起小程序
    const val STRATEGY_C = "C"   // 内置 WebView

    /**
     * 主入口。按策略顺序尝试，失败按需降级，全失败则复制到剪贴板。
     *
     * @param ok 已通过 [ParseResult] 全部校验的结果，携带 launchUrl / 参数 / 标识
     */
    fun launch(
        activity: Activity,
        ok: ParseResult.Ok,
        strategy: String,
        debugMode: Boolean,
        autoFallback: Boolean
    ): AttemptResult {

        val order = buildOrder(activity, strategy, autoFallback)
        QLog.i(
            "Launch",
            "策略顺序 = $order（用户选择=$strategy，形态=${ok.idKind}，自动降级=$autoFallback，调试模式=$debugMode）"
        )

        var lastFailure: AttemptResult? = null

        for (s in order) {
            QLog.i("Launch", "──────── 尝试策略 $s ────────")
            val r = when (s) {
                STRATEGY_A -> tryA(activity, ok, debugMode)
                STRATEGY_B -> tryB(activity, ok, debugMode)
                STRATEGY_C -> tryC(activity, ok, debugMode)
                else -> AttemptResult.Unavailable(s, "未知策略 $s")
            }

            when (r) {
                is AttemptResult.Started -> {
                    QLog.i("Launch", "策略 $s 已发起。component=${r.resolvedComponent} ${r.extra}")
                    return r
                }
                is AttemptResult.PlannedDryRun -> {
                    QLog.i("Launch", "策略 $s 调试模式：仅记录，未唤起。component=${r.resolvedComponent} ${r.extra}")
                    return r
                }
                is AttemptResult.Unavailable -> {
                    QLog.w("Launch", "策略 $s 不可用：${r.reason}")
                    lastFailure = r
                }
                is AttemptResult.Failed -> {
                    QLog.w("Launch", "策略 $s 失败：${r.reason}")
                    lastFailure = r
                }
            }
        }

        // 全失败 → 剪贴板兜底
        QLog.w("Launch", "所有策略均未成功，最后失败=${lastFailure?.let { describe(it) }}，转入剪贴板兜底")
        copyToClipboard(activity, ok.launchUrl)
        return lastFailure ?: AttemptResult.Failed("-", "没有任何可用策略")
    }

    private fun describe(r: AttemptResult): String = when (r) {
        is AttemptResult.Unavailable -> "${r.strategy}:unavailable(${r.reason})"
        is AttemptResult.Failed -> "${r.strategy}:failed(${r.reason})"
        is AttemptResult.Started -> "${r.strategy}:started"
        is AttemptResult.PlannedDryRun -> "${r.strategy}:dryrun"
    }

    /**
     * 计算尝试顺序。这里有两层考虑：
     *
     * 1. **形态**。B 档（alipays 直连）的一步直达**只在形态①（SN）上实测过**。
     *    形态②（NQT）不知道小程序需要哪些 query 参数，所以不把它当默认首选 ——
     *    改成让 C 档先上：C 打开官方包装页，页面自己会发起它自己的 alipays://
     *    跳转，那是官方给的完整参数，比我猜的可靠。
     *
     * 2. **A 档不参与降级**。A 已知只会落到 App 下载引导页，挂在链上等于
     *    把用户丢到下载页。只有用户显式选 A 时才走 A → B → C 的完整链。
     */
    private fun buildOrder(
        activity: Activity,
        strategy: String,
        autoFallback: Boolean
    ): List<String> {
        if (!autoFallback) return listOf(strategy)

        val bUsable = Prefs.alipayAppId(activity).isNotEmpty() && Prefs.alipayPage(activity).isNotEmpty()

        // 2026-11-06 20:09 起，两种形态的 B 档都实测能直达设备页：
        //   形态① SN  → masterSwitch=0&keepUsing=1&SN=<15位>&isShowAd=false
        //   形态② NQT → masterSwitch=0&keepUsing=1&NQT=<UUID>&isShowAd=false
        // 所以不再需要「形态② C 优先」的特殊分支，统一 B 优先。
        return when (strategy) {
            STRATEGY_A -> if (bUsable) listOf(STRATEGY_A, STRATEGY_B, STRATEGY_C)
                          else listOf(STRATEGY_A, STRATEGY_C)

            STRATEGY_B -> if (bUsable) listOf(STRATEGY_B, STRATEGY_C)
                          else listOf(STRATEGY_C)

            STRATEGY_C -> listOf(STRATEGY_C)

            else -> if (bUsable) listOf(STRATEGY_B, STRATEGY_C) else listOf(STRATEGY_C)
        }.distinct()
    }

    // ---------------------------------------------------------------- 策略 A

    /**
     * A：ACTION_VIEW 打开原始 URL，交给系统与厂商官方分发页自行处理。
     * 这是最"干净"的一档：App 完全不解释这个 URL，只是把它递出去。
     *
     * 用 launchUrl 而不是 innerUrl：形态②的机身码是带包装层的完整地址，
     * 包装层可能含一次性参数，重拼反而可能失效。
     */
    private fun tryA(activity: Activity, ok: ParseResult.Ok, debugMode: Boolean): AttemptResult {
        val url = ok.launchUrl
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }

        val resolved = resolve(activity, intent)
        QLog.i("Launch", "A 构造 Intent=${dumpIntent(intent)}")
        QLog.i("Launch", "A resolveActivity=${resolved ?: "null"}")

        if (resolved == null) {
            return AttemptResult.Failed(STRATEGY_A, "没有 App 能处理 https VIEW（resolveActivity=null）")
        }

        if (debugMode) {
            return AttemptResult.PlannedDryRun(STRATEGY_A, intent, resolved, "动作=startActivity(ACTION_VIEW, $url)")
        }

        return start(activity, intent, STRATEGY_A, resolved)
    }

    // ---------------------------------------------------------------- 策略 B

    /**
     * B：经支付宝唤起小程序。
     *
     * 两条内部路径，按顺序试：
     *   B1  alipays://platformapi/startapp?appId=..&page=..&query=..
     *   B2  https://ds.alipay.com/?scheme=<URLEncode(B1 的 uri)>
     *
     * appId / page / query 模板全部来自设置页（有实测默认预填值）。
     * 未配置时本档直接判 Unavailable，不构造半截 URI。
     *
     * query 的构造分形态，两个形态用同一套外层参数，只有标识键名不同：
     *   形态①（SN）  -> masterSwitch=0&keepUsing=1&SN=<15位数字>&isShowAd=false
     *   形态②（NQT） -> masterSwitch=0&keepUsing=1&NQT=<UUID>&isShowAd=false
     *
     * 两次实测教训（都记在 README 第 6.2 节）：
     *   1. 只带 SN（query=SN%3Dxxx）-> 小程序判「二维码有误，换一个试试」；
     *   2. 形态②直传机身码里官方那组 Company/CommunicateType/Ver/NQT
     *      -> 同样落到「二维码有误」。
     * 所以两个模板都不能精简成裸标识，必须带上外层参数。
     */
    private fun tryB(activity: Activity, ok: ParseResult.Ok, debugMode: Boolean): AttemptResult {
        val appId = Prefs.alipayAppId(activity)
        val page = Prefs.alipayPage(activity)

        if (appId.isEmpty() || page.isEmpty()) {
            return AttemptResult.Unavailable(
                STRATEGY_B,
                "未配置 appId/page（appId=${if (appId.isEmpty()) "空" else "已填"}，" +
                    "page=${if (page.isEmpty()) "空" else "已填"}）"
            )
        }

        val query = buildQuery(activity, ok)
        val schemeUri = buildAlipayScheme(appId, page, query)
        QLog.i("Launch", "B 形态=${ok.idKind} 生成的 query = $query")
        QLog.i("Launch", "B 生成的 alipays URI = $schemeUri")

        // --- B1: 直连 alipays scheme ---
        val i1 = Intent(Intent.ACTION_VIEW, Uri.parse(schemeUri))
        val r1 = resolve(activity, i1)
        QLog.i("Launch", "B1 Intent=${dumpIntent(i1)}  resolve=${r1 ?: "null"}")

        if (r1 != null) {
            if (debugMode) {
                return AttemptResult.PlannedDryRun(STRATEGY_B, i1, r1, "子路径=B1(alipays 直连)")
            }
            val started = start(activity, i1, "$STRATEGY_B/B1", r1)
            if (started is AttemptResult.Started) return started
            QLog.w("Launch", "B1 启动抛异常，继续试 B2：${(started as? AttemptResult.Failed)?.reason}")
        }

        // --- B2: ds.alipay.com 包装 ---
        val wrapped = "https://ds.alipay.com/?scheme=" + Uri.encode(schemeUri)
        val i2 = Intent(Intent.ACTION_VIEW, Uri.parse(wrapped)).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        val r2 = resolve(activity, i2)
        QLog.i("Launch", "B2 包装 URL=$wrapped")
        QLog.i("Launch", "B2 Intent=${dumpIntent(i2)}  resolve=${r2 ?: "null"}")

        if (r2 == null) {
            return AttemptResult.Failed(STRATEGY_B, "B1 无接收方，B2 也没有 App 能处理 ds.alipay.com")
        }

        if (debugMode) {
            return AttemptResult.PlannedDryRun(STRATEGY_B, i2, r2, "子路径=B2(ds.alipay.com 包装)")
        }

        return start(activity, i2, "$STRATEGY_B/B2", r2)
    }

    /**
     * 按形态拼 query。
     *
     * 模板里支持的占位符：
     *   {SN}    → 形态①的数字设备号
     *   {NQT}   → 形态②的 UUID
     *   {ID}    → 当前形态的标识值（两者皆可）
     *   {QUERY} → 官方内层地址的**原始 query 整串**（键顺序保持机上原样）
     *
     * 形态②模板为空时直接返回 {QUERY}，即把官方给的那组参数原样带过去，
     * 不添加任何自己编的参数。
     */
    private fun buildQuery(activity: Activity, ok: ParseResult.Ok): String {
        val tpl = if (ok.idKind == ParseResult.ID_NQT) {
            Prefs.alipayQueryTemplateNqt(activity)
        } else {
            Prefs.alipayQueryTemplate(activity)
        }

        if (tpl.isEmpty()) {
            val raw = ok.paramsText()
            QLog.i("Launch", "query 模板为空 → 直传官方 query（${ok.params.size} 个参数）")
            return raw
        }

        var out = tpl
            .replace("{SN}", ok.params[ParseResult.ID_SN] ?: "")
            .replace("{NQT}", ok.params[ParseResult.ID_NQT] ?: "")
            .replace("{ID}", ok.id)
            .replace("{QUERY}", ok.paramsText())

        // 模板写了 isShowAd 才补外层那一个，复刻官方跳转形状
        if (tpl.contains("isShowAd", ignoreCase = true) && !out.trimEnd().endsWith("isShowAd=false")) {
            out = "$out&isShowAd=false"
        }
        return out
    }

    /**
     * 拼 alipays://platformapi/startapp?appId=..&page=..&query=..
     *
     * page 需要 URL 编码（小程序页面路径含 / 与 ?）。
     * query 整体编码一次（它自己内部还有 & 和 =）。
     *
     * 注意：外层 &isShowAd=false 由 [buildQuery] 按模板决定（模板里出现 isShowAd
     * 才补外层那一个），这里不再无条件追加，这样用户清空模板时不会凭空多出参数。
     */
    private fun buildAlipayScheme(appId: String, page: String, query: String): String {
        val encPage = Uri.encode(page, "-_.!~*'()")   // 保留 path 分隔符
        val encQuery = Uri.encode(query)
        return "alipays://platformapi/startapp" +
            "?appId=$appId" +
            "&page=$encPage" +
            "&query=$encQuery"
    }

    // ---------------------------------------------------------------- 策略 C

    /**
     * C：内置 WebView 打开原始 URL，把页面里可能出现的
     *    alipays:// / alipay:// 等 scheme 外抛给支付宝 App。
     *
     * 这一档不解析、不注入、不修改页面任何内容，只是当个浏览器。
     *
     * 对形态②特别重要：机身码外层是第三方跳转包装，包装页自己知道该往哪跳，
     * 让它走完是最不容易出错的做法。
     */
    private fun tryC(activity: Activity, ok: ParseResult.Ok, debugMode: Boolean): AttemptResult {
        val intent = WebViewActivity.newIntent(activity, ok.launchUrl)

        if (debugMode) {
            return AttemptResult.PlannedDryRun(
                STRATEGY_C, intent, WebViewActivity::class.java.name,
                "动作=startActivity(WebViewActivity, ${ok.launchUrl})；UA伪装=${Prefs.useAlipayUa(activity)}"
            )
        }

        val resolved = resolve(activity, intent)
        return start(activity, intent, STRATEGY_C, resolved)
    }

    // ---------------------------------------------------------------- 公共

    /**
     * resolveActivity 封装。Android 11+ 需要 manifest 里的 <queries>，
     * 已在 AndroidManifest.xml 中声明 https / alipays / 支付宝包名。
     */
    private fun resolve(activity: Activity, intent: Intent): String? {
        return try {
            @Suppress("DEPRECATION")
            val ri = activity.packageManager.resolveActivity(
                intent, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
            )
            ri?.activityInfo?.let { "${it.packageName}/${it.name}" }
        } catch (t: Throwable) {
            QLog.e("Launch", "resolveActivity 抛异常", t)
            null
        }
    }

    private fun start(activity: Activity, intent: Intent, label: String, resolved: String?): AttemptResult {
        return try {
            QLog.i("Launch", "$label 即将 startActivity: ${dumpIntent(intent)}")
            activity.startActivity(intent)
            QLog.i("Launch", "$label startActivity 返回正常")
            AttemptResult.Started(label, intent, resolved)
        } catch (t: ActivityNotFoundException) {
            QLog.e("Launch", "$label ActivityNotFoundException", t)
            AttemptResult.Failed(label, "ActivityNotFoundException: ${t.message}")
        } catch (t: SecurityException) {
            QLog.e("Launch", "$label SecurityException（可能被系统/厂商限制）", t)
            AttemptResult.Failed(label, "SecurityException: ${t.message}")
        } catch (t: Throwable) {
            QLog.e("Launch", "$label 未预期异常", t)
            AttemptResult.Failed(label, "${t.javaClass.simpleName}: ${t.message}")
        }
    }

    /** 把 Intent 关键字段拍平成一行，日志可读且够诊断。 */
    fun dumpIntent(i: Intent): String {
        val data = i.dataString ?: "null"
        val comp = i.component?.flattenToShortString() ?: "null"
        val pkg = i.`package` ?: "null"
        val cats = i.categories?.joinToString(",") ?: "[]"
        val flags = "0x${Integer.toHexString(i.flags)}"
        return "action=${i.action} data=$data pkg=$pkg component=$comp categories=$cats flags=$flags"
    }

    /** 兜底：把 URL 放进剪贴板并提示，用户可手动粘贴到浏览器/支付宝。 */
    fun copyToClipboard(activity: Activity, url: String) {
        try {
            val cm = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("QrJump URL", url))
            QLog.i("Launch", "已将 URL 复制到剪贴板：$url")
            Toast.makeText(
                activity,
                "无法唤起，已复制链接到剪贴板\n请手动粘贴到浏览器或支付宝打开",
                Toast.LENGTH_LONG
            ).show()
        } catch (t: Throwable) {
            QLog.e("Launch", "复制剪贴板失败", t)
            Toast.makeText(activity, "无法唤起，且复制失败：${t.message}", Toast.LENGTH_LONG).show()
        }
    }
}
