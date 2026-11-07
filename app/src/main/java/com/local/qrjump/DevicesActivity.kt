package com.local.qrjump

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * 启动页 = 设备列表。
 *
 * ── 为什么把它设成启动页 ──
 * 日常真正高频的动作是"打开 → 选一台 → 到设备页"，而扫码一台新机器是低频动作。
 * 之前把扫码页设成启动页，每次都要等相机起来、再点一下一键直达面板，
 * 一个日常动作被拆成了两步。现在打开就是列表，点一行就走。
 *
 * ── 红线在这里怎么体现 ──
 * 列表里只放**设备标识**（SN / NQT）与用户自己起的备注名。
 * 点击一行做的事和扫到那张码完全一样：走 [Launcher] 的 A/B/C 三档，
 * 把官方地址原样递出去。不产生任何下单、开机、出水、支付指令，
 * 也不携带任何 token / 签名 / 登录态。
 */
class DevicesActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private lateinit var emptyView: TextView

    // 液态玻璃改版后，底部两个按钮与右上角入口都从 Button 换成了 TextView
    // （Material Button 自带实心底色，放不进玻璃背景，会一眼看出是贴上去的色块）。
    // 类型跟着改成 TextView，否则 findViewById<Button> 会抛 ClassCastException。
    private lateinit var btnClear: TextView
    private lateinit var btnAdd: TextView
    private lateinit var a11yWarn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        container = findViewById(R.id.containerDevices)
        emptyView = findViewById(R.id.tvDevicesEmpty)
        btnClear = findViewById(R.id.btnDevicesClear)
        btnAdd = findViewById(R.id.btnAddDevice)
        a11yWarn = findViewById(R.id.tvA11yWarn)

        // 提示条本身就是入口：点一下直达系统无障碍页。
        // 不弹对话框、不解释第二遍 —— 用户点它的动作已经表达了意图。
        a11yWarn.setOnClickListener {
            QLog.i("Devices", "用户点了无障碍提示条，跳系统无障碍设置")
            openA11ySettings()
        }

        btnAdd.setOnClickListener {
            QLog.i("Devices", "去扫码添加设备")
            startActivity(Intent(this, ScanActivity::class.java))
        }

        findViewById<TextView>(R.id.btnDevicesSettings).setOnClickListener {
            QLog.i("Devices", "从设备列表打开设置页")
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        btnClear.setOnClickListener { confirmClear() }

        QLog.i("Devices", "═══ 设备列表（启动页）打开 ═══")
        QLog.i(
            "Devices",
            "策略=${Prefs.strategy(this)} 调试=${Prefs.debugMode(this)} " +
                "自动解锁=${Prefs.autoUnlock(this)} 无障碍服务=${AutoUnlock.isServiceEnabled(this)}"
        )
    }

    override fun onResume() {
        super.onResume()
        // 无障碍自愈放在 onResume 而不是 onCreate（2026-11-06 23:03 实测修正）。
        //
        // 两个理由：
        //  1) MIUI 在「从最近任务划掉卡片」时会把 accessibility_enabled /
        //     enabled_accessibility_services 清空。App 回到前台是最自然的补救时机，
        //     而 onCreate 只在进程第一次起来时跑一次 —— 从后台切回来不经过它。
        //  2) 重装 APK 之后系统会**异步复核**一次包变化并再次清除授权（实测约 1 秒），
        //     所以 onCreate 那一次写回可能被随后的系统动作抹掉；
        //     放进 onResume（每次回前台都会跑）能补到第二次。
        //
        // heal() 自身是幂等的：授权在位时只打一行「无需自愈」就返回。
        if (A11yGuard.heal(this)) {
            QLog.i("Devices", "无障碍授权已确保在位（写回是同步的，服务绑定异步，通常 <1 秒）")
        }
        refreshA11yWarn()
        render()
    }

    /**
     * 决定那条黄色提示条要不要露出来。
     *
     * 三个条件同时满足才显示：用户开了自动解锁、授权现在不在位、而且本应用**补不回来**。
     * 第三点是关键 —— 补得回来的情况，heal() 刚刚已经补好了，
     * 这时候再摆一条「授权没了」就是在骗人。
     */
    private fun refreshA11yWarn() {
        val wanted = Prefs.autoUnlock(this)
        val enabled = A11yGuard.isEnabled(this)
        val writable = A11yGuard.canWrite(this)
        val show = wanted && !enabled && !writable
        a11yWarn.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            QLog.w(
                "Devices",
                "自动解锁开着，但无障碍授权不在位，且本机不允许本应用自己写回" +
                    "（ColorOS 封了 pm grant），改为显示可点的提示条"
            )
        }
    }

    private fun openA11ySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        try {
            startActivity(intent)
        } catch (t: Throwable) {
            QLog.e("Devices", "打开无障碍设置失败", t)
            Toast.makeText(this, R.string.devices_a11y_open_failed, Toast.LENGTH_LONG).show()
        }
    }

    // ------------------------------------------------------------ 渲染

    private fun render() {
        val list = DeviceStore.all(this)
        QLog.i("Devices", "设备簿共 ${list.size} 台：${list.joinToString { "${it.idKind}:${it.id}" }}")

        container.removeAllViews()

        if (list.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            btnClear.visibility = View.GONE
            return
        }
        emptyView.visibility = View.GONE
        btnClear.visibility = View.VISIBLE

        val inflater = LayoutInflater.from(this)
        for (d in list) {
            val row = inflater.inflate(R.layout.item_device, container, false)

            val name = row.findViewById<TextView>(R.id.tvDeviceName)
            val more = row.findViewById<TextView>(R.id.btnDeviceMore)

            name.text = if (d.alias.isNotBlank()) d.alias else defaultName(d)
            // 「⋯」是个装饰性的三点，用文字更稳（不同 MIUI 版本对 icon 支持不一）
            more.text = "⋯"

            row.setOnClickListener {
                QLog.i("Devices", "点选设备：形态=${d.idKind} 标识=${d.id} 备注=${d.alias}")
                launchDevice(d)
            }

            more.setOnClickListener { showRowMenu(d) }

            container.addView(row)
        }
    }

    private fun defaultName(d: DeviceStore.Device): String = if (d.idKind == ParseResult.ID_SN) {
        getString(R.string.quick_id_sn, d.id)
    } else {
        getString(R.string.quick_id_nqt, d.id)
    }

    // ------------------------------------------------------------ 送达

    /**
     * 用这台设备走一遍和扫码完全相同的唤起流程。
     *
     * 注意这里重新 parse 一次，保证交到 Launcher 手里的永远是通过完整校验的结果。
     */
    private fun launchDevice(d: DeviceStore.Device) {
        val ok = when (val r = ParseResult.parse(d.launchUrl)) {
            is ParseResult.Ok -> r
            is ParseResult.Reject -> {
                QLog.w("Devices", "这台设备的地址已不再通过校验，拒绝唤起：${r.reason}")
                Toast.makeText(this, r.reason, Toast.LENGTH_LONG).show()
                return
            }
        }

        val debug = Prefs.debugMode(this)
        val strategy = Prefs.strategy(this)
        val autoFb = Prefs.autoFallback(this)

        QLog.i(
            "Devices",
            "从设备列表唤起：形态=${ok.idKind} 标识=${ok.id} 策略=$strategy 调试=$debug 降级=$autoFb"
        )

        val r = Launcher.launch(this, ok, strategy, debug, autoFb)

        when (r) {
            is AttemptResult.Started -> {
                QLog.i("Devices", "✓ 已发起：${r.strategy} → ${r.resolvedComponent}")
                // 设备簿里把它的时间刷新到最前
                DeviceStore.remember(this, ok)
                // 进入了自动解锁的待命窗口（开关关着时 arm 内部会直接返回）
                AutoUnlock.arm(this)
                finish()
            }

            is AttemptResult.PlannedDryRun -> {
                QLog.i("Devices", "⚠ 调试模式：未真正唤起（${r.strategy}）")
                Toast.makeText(this, getString(R.string.toast_dryrun), Toast.LENGTH_LONG).show()
            }

            is AttemptResult.Unavailable ->
                Toast.makeText(this, getString(R.string.status_failed, r.reason), Toast.LENGTH_LONG).show()

            is AttemptResult.Failed ->
                Toast.makeText(this, getString(R.string.status_failed, r.reason), Toast.LENGTH_LONG).show()
        }
    }

    // ------------------------------------------------------------ 行菜单

    private fun showRowMenu(d: DeviceStore.Device) {
        val items = arrayOf(
            getString(R.string.devices_rename),
            getString(R.string.devices_pin),
            getString(R.string.devices_delete)
        )
        AlertDialog.Builder(this)
            .setTitle(if (d.alias.isNotBlank()) d.alias else defaultName(d))
            .setItems(items) { _, which ->
                when (which) {
                    0 -> askRename(d)
                    1 -> pinDevice(d)
                    2 -> askDelete(d)
                }
            }
            .show()
    }

    /**
     * 放到桌面：给这**一台**设备放一个 1×1 方块。
     *
     * 2026-11-06 23:28 之前这里只调 requestPinShortcut，在 MIUI 上它返回 true
     * 却什么都不画，用户看到的是"点了没反应"。现在改成按桌面能力选通道，
     * 并且**把结果如实告诉用户** —— 成了就说成了，没成也说清楚下一步。
     *
     * 2026-11-07 从「一块列表」改成「一格一台」：列表小部件在桌面上是个面板，
     * 点之前还要在面板里找行；1×1 的位置由用户自己摆，按位置记比按文字记快。
     */
    private fun pinDevice(d: DeviceStore.Device) {
        val label = if (d.alias.isNotBlank()) d.alias else defaultName(d)
        QLog.i("Devices", "请求放到桌面：key=${d.key} label=$label")

        // 第一选择：独立快捷方式（能落到原生/第三方桌面）。
        if (ShortcutHelper.pinToHome(this, d)) {
            Toast.makeText(this, R.string.devices_pin_ok, Toast.LENGTH_LONG).show()
            return
        }

        // 第二选择：请求放这台设备自己的 1×1 小部件。
        // 快捷方式在这台机器的桌面上不可用（MIUI 实测是假成功），
        // 但小部件走的是 AppWidgetService，是另一套机制，实测可用。
        // 设备键会先记进 WidgetBinding 的 pending 槽，配置页取走后立刻返回，
        // 用户只需要在系统那个确认框上点一下。
        QLog.w("Devices", "快捷方式不可用，改为请求放置单设备小部件")
        if (ShortcutHelper.pinWidget(this, d)) {
            Toast.makeText(this, R.string.devices_pin_widget_ok, Toast.LENGTH_LONG).show()
            return
        }

        // 两条都不可用：说清楚下一步，别让用户以为是坏了。
        QLog.w("Devices", "小部件请求也未被接受，提示手动添加")
        Toast.makeText(this, R.string.devices_pin_failed, Toast.LENGTH_LONG).show()
    }

    private fun askRename(d: DeviceStore.Device) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = getString(R.string.devices_rename_hint)
            setText(d.alias)
            setSelection(text.length)
            setPadding(48, 36, 48, 36)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.devices_rename)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                DeviceStore.rename(this, d.key, input.text.toString())
                Toast.makeText(this, R.string.devices_renamed, Toast.LENGTH_SHORT).show()
                render()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun askDelete(d: DeviceStore.Device) {
        AlertDialog.Builder(this)
            .setTitle(R.string.devices_delete)
            .setMessage(if (d.alias.isNotBlank()) d.alias else defaultName(d))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                DeviceStore.forget(this, d.key)
                Toast.makeText(this, R.string.devices_deleted, Toast.LENGTH_SHORT).show()
                render()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun confirmClear() {
        val n = DeviceStore.all(this).size
        if (n == 0) return
        AlertDialog.Builder(this)
            .setTitle(R.string.devices_clear)
            .setMessage(getString(R.string.devices_clear_confirm, n))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                DeviceStore.clear(this)
                Toast.makeText(this, R.string.devices_cleared, Toast.LENGTH_SHORT).show()
                render()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}

