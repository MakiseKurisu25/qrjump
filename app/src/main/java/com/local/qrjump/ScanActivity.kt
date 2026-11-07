package com.local.qrjump

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 扫码页。**不再自动开相机**：进页面先看到一块提示 + 「点击开始扫码」大按钮，
 * 点了才 bindToLifecycle；扫成功后立刻 unbindAll，避免举着手机时白跑分析。
 *
 * 为什么改成按键触发：
 *   启动页已经是设备列表，扫码是低频动作。原来那版一进页面相机就起、还带个
 *   常驻的权限弹窗，等于每次都为一个不常用的动作付代价（耗电、隐私指示灯常亮、
 *   在暗处晃到别人）。改成显式点击后，相机的生命周期严格等于"这次扫码"。
 *
 * 解码器分两层：
 *   1) 首选 ML Kit（精度高、对焦容忍度好）。
 *   2) ML Kit 不可用或连续失败时，自动切 ZXing core（纯本地，无需 GMS）。
 */
class ScanActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var statusText: TextView
    private lateinit var resultPanel: LinearLayout
    private lateinit var resultDetail: TextView
    private lateinit var progress: ProgressBar
    private lateinit var hintText: TextView
    private lateinit var startPanel: LinearLayout
    private lateinit var btnStartScan: TextView

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    /** 已经处理过一帧就不再处理，直到用户点"继续扫描"。 */
    private val consumed = AtomicBoolean(false)

    /** 相机是否已经由用户点按钮启动过。用来防止重复 bind。 */
    private var cameraStarted = false

    private var cameraProvider: ProcessCameraProvider? = null
    private var mlKitScanner: BarcodeScanner? = null

    /**
     * ML Kit 是否可用。构造函数抛错、或运行期连续失败达到阈值，
     * 都会把它置为 false 并永久切到 ZXing。
     */
    @Volatile
    private var useMlKit = true

    /** 运行期失败计数；达到 [ML_KIT_FAIL_THRESHOLD] 就降级。 */
    private val mlKitFailures = java.util.concurrent.atomic.AtomicInteger(0)

    /** ML Kit 运行期连续失败多少次后降级。取 3 是为了容忍偶发失败。 */
    private val ML_KIT_FAIL_THRESHOLD = 3

    /** ZXing 兜底用的 reader，只在 ML Kit 不可用时使用。 */
    private val zxingReader = MultiFormatReader().apply {
        setHints(
            mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(
                    com.google.zxing.BarcodeFormat.QR_CODE
                ),
                DecodeHintType.TRY_HARDER to true,
                DecodeHintType.CHARACTER_SET to "UTF-8"
            )
        )
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                QLog.i("Life", "相机权限已授予")
                startCamera()
            } else {
                QLog.w("Life", "相机权限被拒绝")
                statusText.text = getString(R.string.msg_need_camera)
                hintText.text = getString(R.string.msg_need_camera_hint)
                Toast.makeText(this, R.string.msg_need_camera, Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        previewView = findViewById(R.id.previewView)
        statusText = findViewById(R.id.statusText)
        resultPanel = findViewById(R.id.resultPanel)
        resultDetail = findViewById(R.id.resultDetail)
        progress = findViewById(R.id.scanProgress)
        hintText = findViewById(R.id.hintText)
        startPanel = findViewById(R.id.startPanel)
        btnStartScan = findViewById(R.id.btnStartScan)

        QLog.i("Life", "═══ ScanActivity 启动 ═══")
        QLog.i(
            "Life",
            "策略=${Prefs.strategy(this)}  调试模式=${Prefs.debugMode(this)}  " +
                "自动降级=${Prefs.autoFallback(this)}  自动解锁=${Prefs.autoUnlock(this)}"
        )
        QLog.i(
            "Life",
            "设备=${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} " +
                "Android ${android.os.Build.VERSION.RELEASE} (API ${android.os.Build.VERSION.SDK_INT})"
        )

        // 相机由这个按钮显式启动
        btnStartScan.setOnClickListener {
            QLog.i("Scan", "用户点击「点击开始扫码」")
            startPanel.visibility = View.GONE
            previewView.visibility = View.VISIBLE
            consumed.set(false)
            ensurePermissionThenStart()
        }

        // 返回设备列表
        findViewById<TextView>(R.id.btnScanBack).setOnClickListener {
            QLog.i("Scan", "用户点击返回，退出扫码页")
            finish()
        }

        // 设置入口
        findViewById<TextView>(R.id.btnSettings).setOnClickListener {
            QLog.i("Life", "打开设置页")
            startActivity(android.content.Intent(this, SettingsActivity::class.java))
        }

        // 继续扫描（结果面板上的按钮）
        findViewById<TextView>(R.id.btnRescan).setOnClickListener {
            QLog.i("Scan", "用户点击继续扫描")
            resultPanel.visibility = View.GONE
            hintText.visibility = View.VISIBLE
            previewView.visibility = View.VISIBLE
            statusText.text = getString(R.string.scan_scanning)
            consumed.set(false)
            ensurePermissionThenStart()
        }

        // 重新尝试唤起（用上一次解出的结果）
        findViewById<TextView>(R.id.btnRetryLaunch).setOnClickListener {
            val ok = lastValidOk
            if (ok == null) {
                Toast.makeText(this, R.string.msg_no_last_url, Toast.LENGTH_SHORT).show()
            } else {
                QLog.i("Launch", "用户手动重试唤起：${ok.launchUrl}")
                doLaunch(ok)
            }
        }

        // 调试模式下允许 adb 注入扫码文本：不用举手机对准二维码也能测完整链路。
        consumeInjectedScan()
    }

    /** 调试模式下，允许用 adb 注入一段"假装扫到的文本"，方便不举手机也能测全链路。 */
    private fun consumeInjectedScan() {
        val raw = intent?.getStringExtra(EXTRA_FAKE_SCAN) ?: return
        if (!Prefs.debugMode(this)) {
            QLog.w("Scan", "收到注入的扫码文本，但调试模式未开启，忽略")
            return
        }
        QLog.i("Scan", "收到注入的扫码文本（调试模式）：$raw")
        startPanel.visibility = View.GONE
        onRawText(raw)
    }

    companion object {
        const val EXTRA_FAKE_SCAN = "com.local.qrjump.extra.FAKE_SCAN"
    }

    /** 上一次通过校验的完整结果，供「重新唤起」按钮复用。 */
    private var lastValidOk: ParseResult.Ok? = null

    // ------------------------------------------------------------ 相机

    /** 有权限就直接起；没有就先申请（申请结果回调里再起）。 */
    private fun ensurePermissionThenStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            QLog.i("Life", "相机权限已具备，启动相机")
            startCamera()
        } else {
            QLog.i("Life", "请求相机权限")
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        if (cameraStarted) {
            // 已经在跑：只确保预览可见，不重复 bind（重复 bind 会闪一下并重置对焦）
            QLog.i("Scan", "相机已在运行，跳过重复启动")
            statusText.text = getString(R.string.scan_scanning)
            progress.visibility = View.VISIBLE
            return
        }
        cameraStarted = true
        statusText.text = getString(R.string.scan_starting)
        progress.visibility = View.VISIBLE

        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            try {
                val provider = future.get()
                cameraProvider = provider
                bindUseCases(provider)
            } catch (t: Throwable) {
                QLog.e("Scan", "相机初始化失败", t)
                statusText.text = getString(R.string.scan_camera_failed, t.message ?: "")
                progress.visibility = View.GONE
                startPanel.visibility = View.VISIBLE
                cameraStarted = false
            }
        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindUseCases(provider: ProcessCameraProvider) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        // 目标 1280x720、16:9 优先；设备不支持该尺寸时按"最接近且不小于"回退。
        val resolutionSelector = ResolutionSelector.Builder()
            .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
            .setResolutionStrategy(
                ResolutionStrategy(
                    Size(1280, 720),
                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                )
            )
            .build()

        val analysis = ImageAnalysis.Builder()
            .setResolutionSelector(resolutionSelector)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        // 注意：ImageProxy 的关闭职责下移到 analyzeFrame / decodeWithMlKit。
        // ML Kit 是异步的，若在分析线程里 finally 直接 close，会与 ML Kit 的
        // 后台读取竞争（真机上表现为 Internal error / 帧被提前回收）。
        analysis.setAnalyzer(cameraExecutor) { proxy ->
            try {
                if (consumed.get()) {
                    proxy.close()
                    return@setAnalyzer
                }
                analyzeFrame(proxy)
            } catch (t: Throwable) {
                QLog.e("Scan", "分析帧异常", t)
                runCatching { proxy.close() }
            }
        }

        try {
            provider.unbindAll()
            provider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis
            )
            statusText.text = getString(R.string.scan_scanning)
            QLog.i("Scan", "相机已绑定（后置），分析分辨率 1280x720")
        } catch (t: Throwable) {
            QLog.e("Scan", "bindToLifecycle 失败", t)
            statusText.text = getString(R.string.scan_camera_failed, t.message ?: "")
            progress.visibility = View.GONE
            startPanel.visibility = View.VISIBLE
            cameraStarted = false
        }
    }

    /** 扫到之后 / 离开页面时把相机停掉，别让分析线程空转。 */
    private fun stopCamera(reason: String) {
        try {
            cameraProvider?.unbindAll()
            QLog.i("Scan", "相机已解绑（$reason）")
        } catch (t: Throwable) {
            QLog.w("Scan", "unbindAll 异常：${QLog.describe(t)}")
        }
        progress.visibility = View.GONE
        cameraStarted = false
    }

    // ------------------------------------------------------------ 解码

    /**
     * 单帧分析：先试 ML Kit，失败或不可用时用 ZXing。
     * 注意：这里的 ML Kit 是异步回调，ZXing 是同步。两条路径共享 consumed 标志。
     */
    private fun analyzeFrame(proxy: ImageProxy) {
        if (useMlKit) {
            decodeWithMlKit(proxy)
        } else {
            decodeWithZxing(proxy)
        }
    }

    private fun decodeWithMlKit(proxy: ImageProxy) {
        val mediaImage = proxy.image
        if (mediaImage == null) {
            QLog.w("Scan", "ImageProxy.image == null，跳过该帧")
            proxy.close()
            return
        }

        val scanner = synchronized(this) {
            if (mlKitScanner == null && useMlKit) {
                try {
                    val options = BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build()
                    mlKitScanner = BarcodeScanning.getClient(options)
                    QLog.i("Scan", "ML Kit 解码器初始化成功")
                } catch (t: Throwable) {
                    QLog.e("Scan", "ML Kit 初始化失败，切换到 ZXing 兜底", t)
                    useMlKit = false
                }
            }
            mlKitScanner
        }

        if (scanner == null || !useMlKit) {
            // 本帧改用 ZXing（decodeWithZxing 自己负责 close）
            decodeWithZxing(proxy)
            return
        }

        val image = try {
            InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
        } catch (t: Throwable) {
            QLog.e("Scan", "InputImage 构造失败，本帧交 ZXing", t)
            decodeWithZxing(proxy)
            return
        }

        scanner.process(image)
            .addOnCompleteListener {
                // ML Kit 已读完帧缓冲，此时才能安全释放
                runCatching { proxy.close() }
            }
            .addOnSuccessListener { barcodes ->
                if (consumed.get() || barcodes.isEmpty()) return@addOnSuccessListener
                val raw = barcodes.firstNotNullOfOrNull { it.rawValue }
                if (raw != null) {
                    QLog.i("Scan", "ML Kit 解出原始内容：$raw")
                    consumed.set(true)
                    onRawText(raw)
                }
            }
            .addOnFailureListener { t ->
                val n = mlKitFailures.incrementAndGet()
                QLog.w("Scan", "ML Kit 解析失败（第 $n 次）：${QLog.describe(t)}")
                if (n >= ML_KIT_FAIL_THRESHOLD && useMlKit) {
                    synchronized(this) {
                        if (useMlKit) {
                            useMlKit = false
                            QLog.w(
                                "Scan",
                                "ML Kit 连续失败 $n 次，永久降级到 ZXing 兜底" +
                                    "（本机 GMS 可能缺失或不可用）"
                            )
                            runOnUiThread {
                                Toast.makeText(
                                    this,
                                    R.string.toast_mlkit_fallback,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
    }

    private fun decodeWithZxing(proxy: ImageProxy) {
        try {
            val yPlane = proxy.planes[0]
            val buffer = yPlane.buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)

            val width = proxy.width
            val height = proxy.height
            val rowStride = yPlane.rowStride
            val pixelStride = yPlane.pixelStride

            // 有 padding 的机型需要按行重排成紧密排列
            val tight = if (rowStride == width && pixelStride == 1) {
                data
            } else {
                val out = ByteArray(width * height)
                for (row in 0 until height) {
                    val rowStart = row * rowStride
                    for (col in 0 until width) {
                        val p = rowStart + col * pixelStride
                        out[row * width + col] = if (p < data.size) data[p] else 0
                    }
                }
                out
            }

            val source = PlanarYUVLuminanceSource(tight, width, height, 0, 0, width, height, false)
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            val result = zxingReader.decodeWithState(bitmap)
            val text = result.text
            if (text != null && !consumed.get()) {
                QLog.i("Scan", "ZXing 解出原始内容：$text")
                consumed.set(true)
                onRawText(text)
            }
        } catch (t: com.google.zxing.NotFoundException) {
            // 很常见：这一帧没对准。静默忽略，别刷日志。
        } catch (t: Throwable) {
            QLog.w("Scan", "ZXing 解码异常：${QLog.describe(t)}")
        } finally {
            zxingReader.reset()
            // ZXing 是同步的，读完即可释放
            runCatching { proxy.close() }
        }
    }

    // ------------------------------------------------------------ 校验 + 唤起

    /**
     * 拿到原始码内容后的统一处理：校验 → 记录 → 按策略唤起。
     */
    private fun onRawText(raw: String) {
        QLog.i("Parse", "── 开始校验原始内容 ──")
        QLog.i("Parse", "原始码全文：$raw")

        when (val r = ParseResult.parse(raw)) {
            is ParseResult.Reject -> {
                QLog.w("Parse", "校验失败：${r.reason}")
                runOnUiThread {
                    statusText.text = getString(R.string.scan_rejected)
                    hintText.text = r.reason
                    hintText.visibility = View.VISIBLE
                    Toast.makeText(this, getString(R.string.toast_rejected), Toast.LENGTH_SHORT).show()
                }
                // 让它能继续扫：延迟一会儿再放开
                previewView.postDelayed({ consumed.set(false) }, 1500)
            }

            is ParseResult.Ok -> {
                QLog.i("Parse", "校验通过 launchUrl=${r.launchUrl}")
                QLog.i("Parse", "  形态=${r.idKind}  标识=${r.id}")
                QLog.i("Parse", "  内层 URL=${r.innerUrl}")
                QLog.i("Parse", "  参数=${r.paramsText()}")
                lastValidOk = r
                // 记住这台设备：下次打开 App 就在列表里，不必再对准机身码
                DeviceStore.remember(this, r)
                // 已解出，停相机省电（也防止误扫旁边第二张码）
                stopCamera("已解出二维码")
                runOnUiThread { showResult(r) }
                doLaunch(r)
            }
        }
    }

    private fun showResult(ok: ParseResult.Ok) {
        resultPanel.visibility = View.VISIBLE
        previewView.visibility = View.INVISIBLE
        hintText.visibility = View.GONE
        resultDetail.text = buildString {
            append(getString(R.string.result_id_kind, ok.idKind, ok.id))
            append("\n\n")
            append(getString(R.string.result_url, ok.launchUrl))
            append("\n\n")
            append(getString(R.string.result_params, ok.paramsText()))
            append("\n\n")
            append(getString(R.string.result_strategy, Prefs.strategy(this@ScanActivity)))
            if (Prefs.debugMode(this@ScanActivity)) {
                append("\n")
                append(getString(R.string.result_debug_on))
            }
        }
    }

    private fun doLaunch(ok: ParseResult.Ok) {
        val strategy = Prefs.strategy(this)
        val debug = Prefs.debugMode(this)
        val autoFb = Prefs.autoFallback(this)

        QLog.i(
            "Launch",
            "── 开始唤起 ── 形态=${ok.idKind} 标识=${ok.id} 策略=$strategy 调试=$debug 降级=$autoFb"
        )
        QLog.i("Launch", "launchUrl=${ok.launchUrl}")

        val r = Launcher.launch(this, ok, strategy, debug, autoFb)

        runOnUiThread {
            when (r) {
                is AttemptResult.Started -> {
                    QLog.i("Launch", "✓ 已发起：${r.strategy} → ${r.resolvedComponent}")
                    statusText.text = getString(R.string.status_started, r.strategy)

                    // 进入自动解锁的待命窗口。开关关着时 arm 内部会直接返回，
                    // 所以这里不需要再判一次设置。
                    AutoUnlock.arm(this)

                    // 关键：只有当唤起目标**不在本应用内**时才退到后台。
                    //
                    // 策略 C 拉起的是本应用自己的 WebViewActivity，它和本页在同一个
                    // 任务栈里。此时 moveTaskToBack(true) 会把整个任务（含 WebView）
                    // 一起退到后台，页面随即被系统降频/挂起，官方分发页交付的
                    // alipays:// 跳转就发不出来 —— 表现为"停在 WebView 什么都不发生"。
                    // 实测复现：19:18:54 一键直达 → C → onPageFinished 后一直没有
                    // "[Web] 拦截跳转"，最终落回桌面。
                    val handledInsideApp = r.resolvedComponent
                        ?.startsWith(packageName + "/") == true
                    if (handledInsideApp) {
                        QLog.i("Life", "唤起目标在本应用内（$handledInsideApp），保持前台，等页面自行跳转")
                    } else {
                        previewView.postDelayed({
                            if (!isFinishing) {
                                QLog.i("Life", "唤起完成后将 App 退到后台")
                                moveTaskToBack(true)
                            }
                        }, 1200)
                    }
                }

                is AttemptResult.PlannedDryRun -> {
                    QLog.i("Launch", "⚠ 调试模式：未真正唤起（${r.strategy}）")
                    statusText.text = getString(R.string.status_dryrun, r.strategy)
                    Toast.makeText(
                        this,
                        getString(R.string.toast_dryrun),
                        Toast.LENGTH_LONG
                    ).show()
                    // 调试模式下也照样进待命窗口（前提是自动解锁开关开着），
                    // 这样能在**不真的唤起**的前提下单独验证「自动点击」那条链：
                    // 用 adb 手动 am start 官方 URI 把设备页打开，无障碍服务就能走到
                    // "找到按钮但不点"这一层并留下日志 —— 服务侧同样判 debugMode，
                    // 调试模式下它绝不会真的 performAction。
                    AutoUnlock.arm(this)
                    // 调试模式下不退出，停在结果页方便看日志
                }

                is AttemptResult.Unavailable -> {
                    statusText.text = getString(R.string.status_failed, r.reason)
                }

                is AttemptResult.Failed -> {
                    statusText.text = getString(R.string.status_failed, r.reason)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        QLog.i("Life", "onResume（当前策略=${Prefs.strategy(this)} 调试=${Prefs.debugMode(this)}）")
    }

    override fun onPause() {
        super.onPause()
        // 用户切走时停相机：不留预览在后台空跑
        if (cameraStarted) {
            stopCamera("onPause")
            if (!consumed.get()) {
                startPanel.visibility = View.VISIBLE
                previewView.visibility = View.INVISIBLE
                statusText.text = getString(R.string.scan_stopped)
            }
        }
    }

    override fun onDestroy() {
        QLog.i("Life", "onDestroy，释放解码器与相机")
        try {
            mlKitScanner?.close()
        } catch (t: Throwable) {
            QLog.w("Scan", "关闭 ML Kit 异常：${QLog.describe(t)}")
        }
        try {
            cameraProvider?.unbindAll()
        } catch (t: Throwable) {
            QLog.w("Scan", "unbindAll 异常：${QLog.describe(t)}")
        }
        cameraExecutor.shutdown()
        super.onDestroy()
    }
}


