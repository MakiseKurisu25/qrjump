package com.local.qrjump

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 桌面快捷方式 / 长按图标快捷方式的落点。
 *
 * ── 它做什么 ──
 * 只做一件事：把快捷方式带来的**设备簿键**（`SN|<你的15位SN>` 这种）
 * 在本地设备簿里查出来，然后走和扫码、和设备列表完全相同的那条路：
 * [ParseResult.parse] 重新校验 → [Launcher.launch] 按 A/B/C 递交给官方。
 *
 * ── 它不做什么 ──
 * 不接受任何外部传来的 URL，只接受「设备簿里已经存在的键」。
 * 所以即使有别的 App 构造一个 Intent 打进来，它能做到的最坏情况也只是
 * 「打开某台我已经扫过的机器的官方设备页」——和用户自己点一下完全等价，
 * 不会产生订单、不会开阀出水、也拿不到任何 token。
 *
 * 这也是为什么它可以 exported：真正的安全边界在 [ParseResult] 与
 * [DeviceStore.all] 里，不在 Activity 的 exported 标记上。
 */
class ShortcutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val key = intent?.getStringExtra(EXTRA_KEY)
        QLog.i("Shortcut", "═══ 快捷方式启动 ═══ key=${key ?: "（空）"}")

        if (key.isNullOrBlank()) {
            QLog.w("Shortcut", "没有带设备键，直接结束")
            Toast.makeText(this, R.string.shortcut_bad_key, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val device = DeviceStore.all(this).firstOrNull { it.key == key }
        if (device == null) {
            QLog.w("Shortcut", "设备簿里找不到这个键，可能已被删除：$key")
            Toast.makeText(this, R.string.shortcut_missing, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 与设备列表同一套：重新 parse，拿通过校验的 Ok 再交给 Launcher
        val ok = when (val r = ParseResult.parse(device.launchUrl)) {
            is ParseResult.Ok -> r
            is ParseResult.Reject -> {
                QLog.w("Shortcut", "这台设备的地址已不再通过校验，拒绝唤起：${r.reason}")
                Toast.makeText(this, r.reason, Toast.LENGTH_LONG).show()
                finish()
                return
            }
        }

        val strategy = Prefs.strategy(this)
        val debug = Prefs.debugMode(this)
        val autoFb = Prefs.autoFallback(this)
        QLog.i(
            "Shortcut",
            "从快捷方式唤起：形态=${ok.idKind} 标识=${ok.id} 策略=$strategy 调试=$debug 降级=$autoFb"
        )

        when (val r = Launcher.launch(this, ok, strategy, debug, autoFb)) {
            is AttemptResult.Started -> {
                QLog.i("Shortcut", "✓ 已发起：${r.strategy} → ${r.resolvedComponent}")
                DeviceStore.remember(this, ok)
                // 与设备列表同一套：进入自动解锁的待命窗口（开关关着时 arm 内部直接返回）
                AutoUnlock.arm(this)
                finish()
            }

            is AttemptResult.PlannedDryRun -> {
                QLog.i("Shortcut", "⚠ 调试模式：未真正唤起（${r.strategy}）")
                Toast.makeText(this, getString(R.string.toast_dryrun), Toast.LENGTH_LONG).show()
                finish()
            }

            is AttemptResult.Unavailable ->
                Toast.makeText(this, getString(R.string.status_failed, r.reason), Toast.LENGTH_LONG).show()

            is AttemptResult.Failed ->
                Toast.makeText(this, getString(R.string.status_failed, r.reason), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val EXTRA_KEY = "com.local.qrjump.extra.DEVICE_KEY"
    }
}
