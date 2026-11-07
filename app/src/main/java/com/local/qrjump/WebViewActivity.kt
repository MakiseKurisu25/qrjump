package com.local.qrjump

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 策略 C：内置 WebView。
 *
 * 定位：一个"什么都不改"的浏览器。页面怎么显示就怎么显示，
 * 页面想跳 alipays:// 就把它交给支付宝 App。
 *
 * 明确的边界：
 *   - 不注入 JS，不读取页面 DOM，不抓包，不改写请求体
 *   - 不拦截、不记录、不重放任何网络请求
 *   - 不开 file:// 访问，不允许 http 明文，不允许混合内容
 *   - 不保存表单、不保存密码、不清除也不伪造 cookie
 *
 * 只做一件事：遇到非 http(s) 的 scheme 就外抛。
 */
class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progress: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var fallbackBar: LinearLayout

    private var targetUrl: String = ""
    private var pageErrored = false

    companion object {
        private const val EXTRA_URL = "extra_url"

        fun newIntent(c: Context, url: String): Intent =
            Intent(c, WebViewActivity::class.java).putExtra(EXTRA_URL, url)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webView)
        progress = findViewById(R.id.progress)
        statusText = findViewById(R.id.statusText)
        fallbackBar = findViewById(R.id.fallbackBar)

        targetUrl = intent.getStringExtra(EXTRA_URL).orEmpty()
        if (targetUrl.isEmpty()) {
            QLog.w("Web", "EXTRA_URL 为空，直接结束")
            finish()
            return
        }

        QLog.i("Web", "打开 URL = $targetUrl")

        val useUa = Prefs.useAlipayUa(this)
        val autoScheme = Prefs.webviewAutoScheme(this)
        QLog.i("Web", "配置: 伪装UA=$useUa  外抛scheme=$autoScheme")

        with(webView.settings) {
            javaScriptEnabled = true          // 官方 H5 依赖 JS，关掉页面就是白屏
            domStorageEnabled = true
            databaseEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = false
            displayZoomControls = false
            allowFileAccess = false           // 安全：禁止 file://
            allowContentAccess = false
            javaScriptCanOpenWindowsAutomatically = false
            mediaPlaybackRequiresUserGesture = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = if (useUa) Prefs.customAlipayUa(this@WebViewActivity) else userAgentString
        }
        if (useUa) {
            QLog.i("Web", "UA 已设置为支付宝 UA: ${webView.settings.userAgentString}")
        }

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = handleUrl(request.url, request.isForMainFrame)

            @Deprecated("兼容 API 21-23")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean =
                handleUrl(Uri.parse(url), true)

            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                QLog.i("Web", "onPageStarted: $url")
                progress.visibility = View.VISIBLE
                pageErrored = false
                statusText.text = getString(R.string.web_loading)
            }

            override fun onPageFinished(view: WebView, url: String?) {
                QLog.i("Web", "onPageFinished: $url")
                progress.visibility = View.GONE
                statusText.text = getString(R.string.web_loaded)
                // 加载完成后如果页面明显是空的，也不要报警——交给用户判断，
                // 因为"页面是否有内容"不是本 App 能可靠断言的事。
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: android.webkit.WebResourceError
            ) {
                if (request.isForMainFrame) {
                    pageErrored = true
                    QLog.e("Web", "主文档加载失败: ${error.errorCode} ${error.description} url=${request.url}")
                    progress.visibility = View.GONE
                    statusText.text = getString(R.string.web_error, error.errorCode)
                    fallbackBar.visibility = View.VISIBLE
                }
            }
        }

        webView.webChromeClient = object : android.webkit.WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                progress.progress = newProgress
            }
        }

        statusText.text = getString(R.string.web_loading)
        webView.loadUrl(targetUrl)

        findViewById<Button>(R.id.btnCopy).setOnClickListener {
            Launcher.copyToClipboard(this, targetUrl)
        }
        findViewById<Button>(R.id.btnOpenExternal).setOnClickListener {
            openExternally(targetUrl)
        }
        findViewById<Button>(R.id.btnClose).setOnClickListener { finish() }
    }

    /**
     * 处理页面内的跳转。
     *
     * @return true = 本 WebView 不处理这次跳转（已交给别的 App 或已静默丢弃）
     */
    private fun handleUrl(uri: Uri, isMainFrame: Boolean): Boolean {
        val scheme = uri.scheme?.lowercase()
        QLog.i("Web", "拦截跳转 scheme=$scheme mainFrame=$isMainFrame url=$uri")

        // http(s) 一律留在 WebView 内，不外抛
        if (scheme == "http" || scheme == "https") {
            return false
        }

        if (!Prefs.webviewAutoScheme(this)) {
            QLog.w("Web", "外抛已关闭，丢弃该跳转: $uri")
            return true
        }

        // 其它 scheme（alipays / alipay / taobao ...）交给系统对应的 App
        return try {
            val out = Intent(Intent.ACTION_VIEW, uri).apply {
                if (!isMainFrame) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val ri = packageManager.resolveActivity(
                out, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
            )
            QLog.i("Web", "外抛 Intent=${Launcher.dumpIntent(out)} resolve=${ri?.activityInfo?.let { "${it.packageName}/${it.name}" } ?: "null"}")

            if (ri == null) {
                QLog.w("Web", "没有任何 App 能处理 scheme=$scheme，丢弃")
                Toast.makeText(this, getString(R.string.web_no_handler, scheme), Toast.LENGTH_SHORT).show()
                true
            } else {
                startActivity(out)
                QLog.i("Web", "已外抛给 $scheme")
                // 交给支付宝之后本页就没用了，自己关掉：
                // 既少一次返回，也避免它留在任务栈里被系统降频后干扰自动点击。
                finish()
                true
            }
        } catch (t: Throwable) {
            QLog.e("Web", "外抛 scheme=$scheme 失败", t)
            Toast.makeText(this, getString(R.string.web_scheme_failed, t.message ?: ""), Toast.LENGTH_SHORT).show()
            true
        }
    }

    /** 用系统浏览器打开当前 URL（不经过本 App 的策略逻辑，纯手动）。 */
    private fun openExternally(url: String) {
        try {
            QLog.i("Web", "手动外抛到系统: $url")
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (t: Throwable) {
            QLog.e("Web", "手动外抛失败", t)
            Toast.makeText(this, t.message ?: "打开失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        if (this::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        try {
            if (this::webView.isInitialized) {
                webView.stopLoading()
                webView.webViewClient = WebViewClient()
                webView.destroy()
            }
        } catch (t: Throwable) {
            QLog.e("Web", "销毁 WebView 异常", t)
        }
        super.onDestroy()
    }
}
