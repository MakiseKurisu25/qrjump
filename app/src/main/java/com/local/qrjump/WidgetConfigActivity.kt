package com.local.qrjump

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 1×1 小部件的配置页：决定「这个方块代表哪台设备」。
 *
 * ── 它什么时候出现 ──
 * 小部件的 `android:configure` 指向这里，所以系统在放置小部件时会拉起它。
 * 有两种进入方式：
 *   · 从 App 里点「放到桌面」→ [WidgetBinding] 里已经记了 pending，
 *     这里直接用它绑上、立刻返回，用户看不到这个界面；
 *   · 手动长按桌面 → 添加小部件 → 拖出来 → 没有 pending，
 *     这时才真的弹出来让用户选一台。
 *
 * ── 为什么不做「以后还能改」的入口 ──
 * 官方机制里配置页只在放置时调用一次。放置后要改，用户直接拖掉重新放一个
 * 就行（小部件没有"编辑"这个通用入口，硬做反而要教用户去点两下）。
 * 但如果绑的设备在 App 里被删了，方块会变成「已失效，点一下重新选择」，
 * 那时点它会重新进到这里 —— 这是唯一一个"放置后还能改"的入口。
 */
class WidgetConfigActivity : AppCompatActivity() {

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 默认结果设成"取消"：用户按返回键退出时小部件不该被放下。
        // 这是官方文档明确要求的写法，否则会出现一个什么都不显示的占位方块。
        setResult(Activity.RESULT_CANCELED)

        widgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        QLog.i("Widget", "配置页打开：widgetId=$widgetId 已有绑定=${WidgetBinding.deviceKey(this, widgetId)}")

        // 从「放到桌面」进来的：直接绑、直接结束，别让用户多点一次。
        val pending = WidgetBinding.takePendingPin(this)
        if (pending != null) {
            finishWithBinding(pending, silent = true)
            return
        }

        setContentView(R.layout.activity_widget_config)
        findViewById<TextView>(R.id.tvWidgetConfigBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvWidgetConfigTitle).text =
            getString(R.string.widget_config_title)
        findViewById<TextView>(R.id.tvWidgetConfigHint).text =
            getString(R.string.widget_config_hint)

        renderChoices()
    }

    private fun renderChoices() {
        val container = findViewById<LinearLayout>(R.id.containerWidgetChoices)
        container.removeAllViews()

        val devices = DeviceStore.all(this)
        if (devices.isEmpty()) {
            // 一台都没有：说清楚要先扫码，然后让用户退出。
            // 这里不放"去扫码"按钮 —— 从配置页跳去扫码页，扫完回来小部件
            // 已经因为 Activity 被回收而拿不到 widgetId 了，容易做出一个空壳。
            findViewById<TextView>(R.id.tvWidgetConfigEmpty).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvWidgetConfigEmpty).text =
                getString(R.string.widget_config_empty)
            return
        }
        findViewById<TextView>(R.id.tvWidgetConfigEmpty).visibility = View.GONE

        val current = WidgetBinding.deviceKey(this, widgetId)
        devices.forEach { d ->
            val row = layoutInflater.inflate(R.layout.item_widget_choice, container, false)
            row.findViewById<TextView>(R.id.tvChoiceName).text =
                if (d.alias.isNotBlank()) d.alias else defaultName(d)
            row.findViewById<TextView>(R.id.tvChoiceSub).text = defaultName(d)
            row.findViewById<TextView>(R.id.tvChoiceCheck).visibility =
                if (d.key == current) View.VISIBLE else View.INVISIBLE
            row.setOnClickListener { finishWithBinding(d.key, silent = false) }
            container.addView(row)
        }
        QLog.i("Widget", "配置页列出 ${devices.size} 台设备")
    }

    private fun finishWithBinding(deviceKey: String, silent: Boolean) {
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            QLog.w("Widget", "配置页拿不到 widgetId，无法绑定")
            if (!silent) Toast.makeText(this, R.string.widget_config_failed, Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        WidgetBinding.bind(this, widgetId, deviceKey)
        DeviceWidgetProvider.updateOne(this, widgetId)

        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, result)
        QLog.i("Widget", "配置完成：widgetId=$widgetId -> $deviceKey（静默=$silent）")
        finish()
    }

    private fun defaultName(d: DeviceStore.Device): String = if (d.idKind == ParseResult.ID_SN) {
        getString(R.string.quick_id_sn, d.id.takeLast(6))
    } else {
        getString(R.string.quick_id_nqt, d.id.take(8))
    }
}
