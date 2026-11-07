package com.local.qrjump

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * 设置页。所有会影响唤起行为的开关都在这里，方便逐个试。
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var rgStrategy: RadioGroup
    private lateinit var rbA: RadioButton
    private lateinit var rbB: RadioButton
    private lateinit var rbC: RadioButton
    private lateinit var cbDebug: CheckBox
    private lateinit var cbAutoFallback: CheckBox
    private lateinit var cbAutoUnlock: CheckBox
    private lateinit var etAppId: EditText
    private lateinit var etPage: EditText
    private lateinit var etQuery: EditText
    private lateinit var etQueryNqt: EditText
    private lateinit var etSn: EditText
    private lateinit var cbAlipayUa: CheckBox
    private lateinit var etUa: EditText
    private lateinit var cbWebScheme: CheckBox
    private lateinit var tvResolve: TextView
    private lateinit var tvAutoDelay: TextView
    private lateinit var sbAutoDelay: SeekBar
    private lateinit var tvA11yStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        rgStrategy = findViewById(R.id.rgStrategy)
        rbA = findViewById(R.id.rbA)
        rbB = findViewById(R.id.rbB)
        rbC = findViewById(R.id.rbC)
        cbDebug = findViewById(R.id.cbDebug)
        cbAutoFallback = findViewById(R.id.cbAutoFallback)
        cbAutoUnlock = findViewById(R.id.cbAutoUnlock)
        etAppId = findViewById(R.id.etAppId)
        etPage = findViewById(R.id.etPage)
        etQuery = findViewById(R.id.etQuery)
        etQueryNqt = findViewById(R.id.etQueryNqt)
        etSn = findViewById(R.id.etSn)
        cbAlipayUa = findViewById(R.id.cbAlipayUa)
        etUa = findViewById(R.id.etUa)
        cbWebScheme = findViewById(R.id.cbWebScheme)
        tvResolve = findViewById(R.id.tvResolve)
        tvAutoDelay = findViewById(R.id.tvAutoDelay)
        sbAutoDelay = findViewById(R.id.sbAutoDelay)

        loadIntoViews()

        rgStrategy.setOnCheckedChangeListener { _, id ->
            val s = when (id) {
                R.id.rbB -> Launcher.STRATEGY_B
                R.id.rbC -> Launcher.STRATEGY_C
                else -> Launcher.STRATEGY_A
            }
            Prefs.setStrategy(this, s)
            QLog.i("Life", "设置：策略切换为 $s")
            refreshResolveInfo()
        }

        cbDebug.setOnCheckedChangeListener { _, checked ->
            Prefs.setDebugMode(this, checked)
            QLog.i("Life", "设置：调试模式=$checked")
        }

        cbAutoFallback.setOnCheckedChangeListener { _, checked ->
            Prefs.setAutoFallback(this, checked)
            QLog.i("Life", "设置：自动降级=$checked")
        }

        cbAutoUnlock.setOnCheckedChangeListener { _, checked ->
            Prefs.setAutoUnlock(this, checked)
            QLog.i("Life", "设置：自动解锁=$checked 无障碍服务=${AutoUnlock.isServiceEnabled(this)}")
            if (checked && !AutoUnlock.isServiceEnabled(this)) {
                // 只是提醒，不强行跳转：用户可能正在改别的设置
                Toast.makeText(this, R.string.toast_accessibility_needed, Toast.LENGTH_LONG).show()
            }
        }

        // 等待滑杆：进度 0–30，每格 1 秒。默认 8 秒。
        sbAutoDelay.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar?, progress: Int, fromUser: Boolean) {
                val sec = progress * DELAY_STEP_SEC
                tvAutoDelay.text = getString(R.string.settings_auto_delay, sec)
                if (fromUser) {
                    // 拖一下就落盘，不用等"保存"按钮：这个值用户在试的时候会反复调
                    Prefs.setAutoUnlockDelaySec(this@SettingsActivity, sec)
                    QLog.i("Life", "设置：自动点击等待=${sec} 秒")
                }
            }

            override fun onStartTrackingTouch(bar: SeekBar?) = Unit
            override fun onStopTrackingTouch(bar: SeekBar?) = Unit
        })

        findViewById<Button>(R.id.btnOpenAccessibility).setOnClickListener {
            QLog.i("Life", "跳转系统无障碍设置")
            try {
                startActivity(android.content.Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } catch (t: Throwable) {
                QLog.e("Life", "打开无障碍设置失败", t)
                Toast.makeText(this, R.string.toast_accessibility_needed, Toast.LENGTH_LONG).show()
            }
        }

        // 自愈：把被系统清掉的无障碍授权写回去（需要 WRITE_SECURE_SETTINGS）
        tvA11yStatus = findViewById(R.id.tvA11yStatus)
        findViewById<Button>(R.id.btnHealAccessibility).setOnClickListener {
            QLog.i("A11y", "设置页点了「尝试自动恢复无障碍授权」")
            val ok = A11yGuard.heal(this)
            refreshA11yStatus()
            val msg = when {
                ok -> getString(R.string.a11y_heal_ok)
                A11yGuard.canWrite(this) -> getString(R.string.a11y_heal_failed)
                else -> getString(R.string.a11y_heal_no_perm)
            }
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        refreshA11yStatus()

        cbAlipayUa.setOnCheckedChangeListener { _, checked ->
            Prefs.setUseAlipayUa(this, checked)
            QLog.i("Life", "设置：策略C 伪装支付宝UA=$checked")
        }

        cbWebScheme.setOnCheckedChangeListener { _, checked ->
            Prefs.setWebviewAutoScheme(this, checked)
            QLog.i("Life", "设置：策略C 外抛 scheme=$checked")
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            Prefs.setAlipayAppId(this, etAppId.text.toString())
            Prefs.setAlipayPage(this, etPage.text.toString())
            Prefs.setAlipayQueryTemplate(this, etQuery.text.toString())
            Prefs.setAlipayQueryTemplateNqt(this, etQueryNqt.text.toString())
            Prefs.setCustomAlipayUa(this, etUa.text.toString())
            QLog.i(
                "Life",
                "设置已保存：appId=${mask(Prefs.alipayAppId(this))} " +
                    "page=${Prefs.alipayPage(this)} " +
                    "querySN=${Prefs.alipayQueryTemplate(this)} " +
                    "queryNQT=${Prefs.alipayQueryTemplateNqt(this).ifEmpty { "（空=直传官方）" }}"
            )
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
            refreshResolveInfo()
        }

        findViewById<Button>(R.id.btnTestA).setOnClickListener {
            testLaunch(Launcher.STRATEGY_A)
        }
        findViewById<Button>(R.id.btnTestB).setOnClickListener {
            testLaunch(Launcher.STRATEGY_B)
        }
        findViewById<Button>(R.id.btnTestC).setOnClickListener {
            testLaunch(Launcher.STRATEGY_C)
        }

        findViewById<Button>(R.id.btnShowResolve).setOnClickListener {
            refreshResolveInfo()
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.settings_reset)
                .setMessage(R.string.settings_reset_confirm)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    Prefs.setStrategy(this, Launcher.STRATEGY_B)
                    Prefs.setDebugMode(this, false)
                    Prefs.setAutoFallback(this, true)
                    Prefs.setAlipayAppId(this, Prefs.DEFAULT_APP_ID)
                    Prefs.setAlipayPage(this, Prefs.DEFAULT_PAGE)
                    Prefs.setAlipayQueryTemplate(this, Prefs.DEFAULT_QUERY_TEMPLATE)
                    Prefs.setAlipayQueryTemplateNqt(this, Prefs.DEFAULT_QUERY_TEMPLATE_NQT)
                    Prefs.setUseAlipayUa(this, true)
                    Prefs.setCustomAlipayUa(this, "")
                    Prefs.setWebviewAutoScheme(this, true)
                    Prefs.setAutoUnlock(this, false)
                    Prefs.setAutoUnlockDelaySec(this, Prefs.DEFAULT_AUTO_UNLOCK_DELAY_SEC)
                    QLog.i("Life", "设置已重置为默认值（策略 B + 实测 appId/page/query）")
                    loadIntoViews()
                    refreshResolveInfo()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

        refreshResolveInfo()
    }

    private fun loadIntoViews() {
        when (Prefs.strategy(this)) {
            Launcher.STRATEGY_B -> rbB.isChecked = true
            Launcher.STRATEGY_C -> rbC.isChecked = true
            else -> rbA.isChecked = true
        }
        cbDebug.isChecked = Prefs.debugMode(this)
        cbAutoFallback.isChecked = Prefs.autoFallback(this)
        cbAutoUnlock.isChecked = Prefs.autoUnlock(this)
        // 滑杆位置就是秒数：进度 0–30 ↔ 0–30 秒
        val delaySec = Prefs.autoUnlockDelaySec(this).coerceIn(0, Prefs.MAX_AUTO_UNLOCK_DELAY_SEC)
        sbAutoDelay.progress = delaySec / DELAY_STEP_SEC
        tvAutoDelay.text = getString(R.string.settings_auto_delay, delaySec)
        cbAlipayUa.isChecked = Prefs.useAlipayUa(this)
        cbWebScheme.isChecked = Prefs.webviewAutoScheme(this)
        etAppId.setText(Prefs.alipayAppId(this))
        etPage.setText(Prefs.alipayPage(this))
        etQuery.setText(Prefs.alipayQueryTemplate(this))
        etQueryNqt.setText(Prefs.alipayQueryTemplateNqt(this))
        etUa.setText(Prefs.customAlipayUa(this))
        etSn.hint = getString(R.string.settings_sn_hint)
    }

    /**
     * 用设置页里填的内容做测试。支持三种输入：
     *   1) 纯 SN（形态①）：拼成 https://h5.qiekj.com/skip?SN=...
     *   2) 纯 NQT（形态②）：拼成 https://h5.qiekj.com/skip?Company=qiekj&CommunicateType=1&Ver=4&NQT=...
     *   3) 整条二维码链接（原样解析，会走完整校验，含 link.wtturl.cn 解包）
     *
     * 这里只拼字符串 / 解析字符串，**不发任何网络请求**，也不校验设备是否真实存在。
     */
    private fun testLaunch(strategy: String) {
        val input = etSn.text.toString().trim()
        if (input.isEmpty()) {
            Toast.makeText(this, R.string.settings_need_sn, Toast.LENGTH_SHORT).show()
            return
        }

        val parsed = ParseResult.fromUserInput(input)
        if (parsed is ParseResult.Reject) {
            Toast.makeText(
                this,
                getString(R.string.settings_sn_invalid) + "\n" + parsed.reason,
                Toast.LENGTH_LONG
            ).show()
            QLog.w("Launch", "设置页测试输入无效：${parsed.reason}")
            return
        }

        val ok = parsed as ParseResult.Ok
        QLog.i("Launch", "═══ 设置页测试唤起 ═══ 策略=$strategy 形态=${ok.idKind} 标识=${ok.id}")
        QLog.i("Launch", "launchUrl=${ok.launchUrl}")

        val debug = Prefs.debugMode(this)
        val r = Launcher.launch(this, ok, strategy, debug, false)
        val msg = when (r) {
            is AttemptResult.Started -> "已发起：${r.strategy}\ncomponent=${r.resolvedComponent}"
            is AttemptResult.PlannedDryRun -> "调试模式：未唤起\n${r.extra}\ncomponent=${r.resolvedComponent}"
            is AttemptResult.Unavailable -> "不可用：${r.reason}"
            is AttemptResult.Failed -> "失败：${r.reason}"
        }
        tvResolve.text = msg
        QLog.i("Launch", "测试结果：$msg")
    }

    /** 把当前配置下三种 Intent 的 resolveActivity 结果直接显示出来，省得只看日志。 */
    private fun refreshResolveInfo() {
        val input = etSn.text.toString().trim()
        val ok = run {
            val p = if (input.isEmpty()) {
                ParseResult.fromUserInput("000000000000000")
            } else {
                ParseResult.fromUserInput(input)
            }
            p as? ParseResult.Ok
        }

        val sb = StringBuilder()
        val sampleNote = if (ok == null) "（测试输入无效，以下用占位符演示）" else ""
        sb.append(resolveLine("A  https VIEW", buildA(ok?.launchUrl ?: "https://h5.qiekj.com/skip?SN=000000000000000")))
        if (ok != null) {
            sb.append("\n   A 将打开：").append(ok.launchUrl)
        }
        if (sampleNote.isNotEmpty()) sb.append("\n   ").append(sampleNote)

        val appId = Prefs.alipayAppId(this)
        val page = Prefs.alipayPage(this)

        if (appId.isNotEmpty() && page.isNotEmpty()) {
            val q = buildQueryForPreview(ok)
            val b1 = android.content.Intent(
                android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse(
                    "alipays://platformapi/startapp?appId=$appId" +
                        "&page=${android.net.Uri.encode(page, "-_.!~*'()")}" +
                        "&query=${android.net.Uri.encode(q)}"
                )
            )
            sb.append("\n").append(resolveLine("B1 alipays", b1))
            sb.append("\n   B1 的 query = ").append(q.ifEmpty { "（空）" })

            val wrapped = "https://ds.alipay.com/?scheme=" +
                android.net.Uri.encode(b1.dataString ?: "")
            sb.append("\n").append(
                resolveLine(
                    "B2 ds.alipay",
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(wrapped)
                    )
                )
            )
        } else {
            sb.append("\nB  alipays    —— 未配置 appId/page，本档不可用")
        }

        sb.append("\nC  WebView     —— 本应用内部，无需 resolve")
        sb.append("\n   C 将打开：").append(ok?.launchUrl ?: "（无效输入）")
        sb.append("\n   C 伪装支付宝UA = ").append(Prefs.useAlipayUa(this))

        // 形态②：2026-11-06 20:09 起 B 档也已实测直达，不再提示顺序会变
        if (ok != null && ok.idKind == ParseResult.ID_NQT) {
            sb.append("\n\n注：形态②（NQT）的 B 档已于 2026-11-06 20:09 实测直达热水设备页，")
            sb.append("\n与形态①一样 B 优先，C 作为降级。")
        }

        tvResolve.text = sb.toString()
        QLog.i("Launch", "resolve 面板刷新：\n$sb")
    }

    /** 预览用：按当前形态估一个 query（与 Launcher.buildQuery 的规则保持一致）。 */
    private fun buildQueryForPreview(ok: ParseResult.Ok?): String {
        if (ok == null) return Prefs.alipayQueryTemplate(this).replace("{SN}", "000000000000000")

        val tpl = if (ok.idKind == ParseResult.ID_NQT) {
            Prefs.alipayQueryTemplateNqt(this)
        } else {
            Prefs.alipayQueryTemplate(this)
        }
        if (tpl.isEmpty()) return ok.paramsText()

        var out = tpl
            .replace("{SN}", ok.params[ParseResult.ID_SN] ?: "")
            .replace("{NQT}", ok.params[ParseResult.ID_NQT] ?: "")
            .replace("{ID}", ok.id)
            .replace("{QUERY}", ok.paramsText())
        if (tpl.contains("isShowAd", ignoreCase = true) && !out.trimEnd().endsWith("isShowAd=false")) {
            out = "$out&isShowAd=false"
        }
        return out
    }

    private fun buildA(url: String): android.content.Intent =
        android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))

    private fun resolveLine(label: String, i: android.content.Intent): String {
        val ri = packageManager.resolveActivity(
            i, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
        )
        val target = ri?.activityInfo?.let { "${it.packageName}/${it.name}" } ?: "null（无接收方）"
        return "$label -> $target"
    }

    /** 日志里别把 appId 全打出来，留头尾即可。 */
    private fun mask(s: String): String =
        if (s.length <= 6) s else "${s.take(3)}***${s.takeLast(3)}"

    /** 把无障碍授权的真实状态（含能不能自愈）显示出来，别让用户猜。 */
    private fun refreshA11yStatus() {
        val line = A11yGuard.statusLine(this)
        tvA11yStatus.text = line
        QLog.i("A11y", "设置页状态：$line")
    }

    private companion object {
        /** 稳定等待滑杆每格 1 秒，进度 0–30 对应 0–30 秒。 */
        const val DELAY_STEP_SEC = 1
    }
}