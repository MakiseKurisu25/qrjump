package com.local.qrjump

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 无障碍服务：**只在很窄的条件下**替用户点一下官方设备页上的「立即使用」。
 *
 * ── 为什么用无障碍，而不是别的路子 ──
 * 支付宝小程序跑在支付宝自己的进程里（`com.eg.android.AlipayGphone`），
 * 本应用拿不到它的界面、也注入不了任何东西。唯一被 Android 官方允许的、
 * 不越狱不逆向的「代用户点一下」的机制就是无障碍服务。
 * 它看到的只是系统公开给辅助功能的控件树，我们不读它的网络、不读它的存储。
 *
 * ── 动手的四个必要条件，缺一不可 ──
 *   1. 用户**在设置里打开了**「自动解锁」开关（Prefs.autoUnlock）
 *   2. 处在 [AutoUnlock] 的待命窗口内（刚由本应用唤起，2 分钟内）
 *   3. 发出事件的窗口属于支付宝（`com.eg.android.AlipayGphone`）——
 *      别的应用哪怕有同名按钮也绝不碰
 *   4. 同一棵控件树里同时能找到「出水口」这个只有设备页才有的标记，
 *      避免把别的页面上的同名按钮误判成设备页
 *
 * 四条都满足才点，点完立刻退出待命，一台设备只点一次。
 *
 * ── 两种点击方式（2026-11-06 21:05 实测得到的分支）──
 * 用 `uiautomator dump` 把真实设备页的控件树抓下来看过：
 *
 *   node text="立即使用" class=android.widget.TextView clickable=false bounds=[44,2128][1036,2274]
 *     ^L1 class=android.view.View        clickable=false
 *     ^L2 class=android.view.View        clickable=false
 *     ^L3 class=android.view.View rid=__react-content clickable=false
 *     ^L4 class=android.webkit.WebView   clickable=false
 *     ^L5 class=com.alipay.mywebview.sdk.WebView clickable=false
 *     ^L6 class=android.widget.FrameLayout clickable=false
 *
 * 也就是说这是小程序页面里的一块 WebView 渲染出来的 React 内容，
 * **按钮和它往上的六层祖先全都不是 clickable**，`performAction(ACTION_CLICK)`
 * 对它没有意义。所以点击走两条路：
 *   · 先按老办法找可点击的自身/祖先，有就 performAction；
 *   · 没有就用 `dispatchGesture` 在那个节点的屏幕坐标中心**按下并抬起一次**，
 *     跟人手指点一下完全等价（这也是系统给辅助功能的正规接口，
 *     需要配置里的 `canPerformGestures="true"`）。
 *
 * ── 不做什么 ──
 * 不读屏幕上的账号、手机号、余额、优惠券；不遍历无关节点做记录；
 * 不做滑动、输入、返回等任何其它手势；不常驻后台做别的事。
 */
class AutoUnlockService : AccessibilityService() {

    private companion object {
        /** 官方页面上的按钮文案。实测两种形态的设备页都是这四个字。 */
        const val TARGET_TEXT = "立即使用"

        /** 设备页独有的标记。有它才说明这是设备页，而不是随便某个有「立即使用」的页面。 */
        const val DEVICE_PAGE_MARK = "出水口"

        /**
         * 「页面还没准备好」的标记。设备页在拉设备状态的那几秒里会显示「内容准备中」
         * 加一个「刷新」按钮，而**这时候「立即使用」已经在控件树里了**
         * （2026-11-06 21:04 的 dump 里两者同时存在）。看到这些字样就说明还不能点。
         */
        val LOADING_MARKS = listOf("内容准备中", "正在加载", "加载中")

        /** 支付宝包名。只在这个窗口里动手。 */
        const val ALIPAY_PKG = "com.eg.android.AlipayGphone"

        /** 两次尝试之间的最小间隔，避免页面还在渲染时疯狂判断。 */
        const val MIN_INTERVAL_MS = 400L

        /** 手势按下到抬起的时长。太短会被系统当成抖动丢掉，60ms 与真人点击相当。 */
        const val TAP_DURATION_MS = 60L

        /**
         * 轮询间隔。设备页渲染完就不再发无障碍事件，只能自己定时看；
         * 1 秒一次足够跟上一个"加载有延迟"的页面，又不会明显耗电。
         * 只在待命窗口内（最长 2 分钟）跑，点过或超时立刻停。
         */
        const val POLL_INTERVAL_MS = 1000L
    }

    private var lastCheckAt = 0L

    /** 上一条日志的内容，用来去掉同一状态下的重复刷屏。 */
    private var lastNote = ""

    override fun onServiceConnected() {
        super.onServiceConnected()
        // 写一条"我真的被系统绑上了"的回执。设备列表页读它来判断服务状态，
        // 因为 Settings.Secure 的 enabled_accessibility_services 普通应用读不到。
        Prefs.setA11yConnected(this, true)
        QLog.i("Auto", "无障碍服务已连接（canPerformGestures 见配置）")

        // 2026-11-06 22:11 实测到的一个缺口：轮询原本只在收到无障碍事件时才启动，
        // 而设备页渲染完就静止了。如果服务是在页面已经静止**之后**才被绑上
        // （重装 APK 后重新授权、或用户在系统设置里刚打开开关），就一条事件也收不到，
        // 轮询永远不启动，自动点击等于没开。
        // 所以绑定成功时，只要（1）自动解锁开着（2）处在待命窗口内，就直接起轮询。
        if (Prefs.autoUnlock(this) && AutoUnlock.isArmed() && AutoUnlock.canStillClaim()) {
            QLog.i("Auto", "绑定后发现仍在待命窗口内，直接启动轮询（不依赖事件）")
            startPolling()
            checkOnce()
        }
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        Prefs.setA11yConnected(this, false)
        QLog.i("Auto", "无障碍服务已解绑")
        stopPolling()
        return super.onUnbind(intent)
    }

    /**
     * 无障碍事件只是"可能该看一眼"的信号，真正的判断在 [checkOnce]。
     *
     * ── 为什么不能只靠事件（2026-11-06 21:50 实测）──
     * 设备页把内容渲染完之后就**不再发任何 TYPE_WINDOW_CONTENT_CHANGED**了。
     * 日志停在 `设备页还在加载（「内容准备中」），等它稳定` 之后就一片安静，
     * 于是"等下一次事件再判"永远等不到。所以这里除了事件，还起了一个
     * 轻量轮询：只要还在待命窗口内，就每 [POLL_INTERVAL_MS] 毫秒自己看一眼。
     * 待命窗口最长 2 分钟，且一旦点过或超时就停，不会常驻耗电。
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val e = event ?: return

        // 只在"窗口内容变化"这类事件上判断，滚动/通知之类直接跳过
        when (e.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> Unit
            else -> return
        }

        if (!Prefs.autoUnlock(this)) return
        if (!AutoUnlock.isArmed()) {
            stopPolling()
            return
        }
        if (!AutoUnlock.canStillClaim()) {
            stopPolling()
            return
        }

        // 事件必须来自支付宝。e.packageName 可能为 null（合成事件），直接跳过。
        val pkg = e.packageName?.toString() ?: return
        if (pkg != ALIPAY_PKG) return

        startPolling()
        checkOnce()
    }

    /** 轮询：设备页静止后不发事件，只能自己定时看。 */
    private val pollHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var polling = false
    private val pollRunnable = object : Runnable {
        override fun run() {
            if (!polling) return
            if (!Prefs.autoUnlock(this@AutoUnlockService) ||
                !AutoUnlock.isArmed() ||
                !AutoUnlock.canStillClaim()
            ) {
                stopPolling()
                return
            }
            checkOnce()
            if (polling) pollHandler.postDelayed(this, POLL_INTERVAL_MS)
        }
    }

    private fun startPolling() {
        if (polling) return
        polling = true
        QLog.i("Auto", "开始轮询设备页状态（每 ${POLL_INTERVAL_MS}ms 一次）")
        pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS)
    }

    private fun stopPolling() {
        if (!polling) return
        polling = false
        pollHandler.removeCallbacks(pollRunnable)
        QLog.i("Auto", "停止轮询")
    }

    /** 看一眼当前窗口，够条件就动手。事件与轮询都走这里，保证判断只有一份。 */
    private fun checkOnce() {
        if (!Prefs.autoUnlock(this)) return
        if (!AutoUnlock.isArmed()) return
        if (!AutoUnlock.canStillClaim()) return

        val now = android.os.SystemClock.elapsedRealtime()
        if (now - lastCheckAt < MIN_INTERVAL_MS) return
        lastCheckAt = now

        val root = rootInActiveWindow ?: return
        try {
            val marks = scanPage(root)
            if (!marks.hasDeviceMark) {
                note("当前窗口没有「$DEVICE_PAGE_MARK」，不像设备页，不动手")
                return
            }
            // 给计时一个起点：从"第一次认出这是设备页"开始算，之后不再重置。
            AutoUnlock.noteDevicePageSeen()
            // 加载标记只用于日志。实测「内容准备中」是页面上独立的小票/广告位
            // 慢区域，等它等于永远等（21:47 那次数了 15 秒仍在）。真正决定
            // 什么时候动手的是下面那条单一时钟：认出设备页起等满设定秒数。
            marks.loadingMark?.let { AutoUnlock.noteLoading(it) }

            val button = marks.target
            if (button == null) {
                note("设备页已出现，但还没找到「$TARGET_TEXT」按钮")
                return
            }

            // 单一时钟：认出设备页起等满 autoUnlockDelaySec 秒（默认 8 秒）。
            val delaySec = Prefs.autoUnlockDelaySec(this)
            if (!AutoUnlock.isSettledEnough(delaySec)) return

            // 调试模式下只报告、不动手。这是唯一能"完整验证这条链"又不真的
            // 开始出水计费的办法：日志会证明"设备页认出来了、加载完了、按钮找到了、
            // 坐标算对了"，只是不真的按下。
            if (Prefs.debugMode(this)) {
                val r = Rect().also { button.getBoundsInScreen(it) }
                QLog.i(
                    "Auto",
                    "调试模式：已定位到「$TARGET_TEXT」（class=${button.className}，" +
                        "clickable=${button.isClickable}，中心=(${r.exactCenterX()}, ${r.exactCenterY()})，" +
                        "已等 ${AutoUnlock.waitedMs() / 1000} 秒 / 阈值 ${delaySec} 秒），本应点击但未动手"
                )
                // 调试模式已经拿到要的证据了，停掉轮询免得每秒刷一行同样的日志
                stopPolling()
                return
            }

            if (!AutoUnlock.claimOnce()) return
            clickNode(button)
        } catch (t: Throwable) {
            QLog.e("Auto", "自动点击过程异常", t)
        } finally {
            // 节点用完要回收，避免跨进程句柄泄漏。
            // recycle() 在 API 33 起被标记废弃（系统自动池化），但低版本仍需手动回收，
            // 所以这里保留调用并显式压制废弃警告。
            @Suppress("DEPRECATION")
            runCatching { root.recycle() }
        }
    }

    /** 同一条提示只打一次，页面纹丝不动时不要每 400ms 刷一行。 */
    private fun note(msg: String) {
        if (msg != lastNote) {
            lastNote = msg
            QLog.i("Auto", msg)
        }
    }

    /**
     * 点这个节点。两条路：
     *   1. 节点自身或往上 8 层祖先里有 enabled 且 clickable 的 → `performAction(ACTION_CLICK)`
     *   2. 都没有（WebView 里的文字按钮就是这种）→ 用 `dispatchGesture` 在节点中心点一下
     */
    private fun clickNode(node: AccessibilityNodeInfo) {
        var cur: AccessibilityNodeInfo? = node
        var guard = 0
        while (cur != null && guard < 8) {
            if (cur.isClickable && cur.isEnabled) {
                val ok = cur.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                QLog.i("Auto", "点击「$TARGET_TEXT」用 ACTION_CLICK 结果=$ok（class=${cur.className}）")
                if (ok) AutoUnlock.disarm("已自动点击「$TARGET_TEXT」")
                return
            }
            cur = cur.parent
            guard++
        }

        // 走到这里说明整条祖先链都不是 clickable，只能点坐标。
        val rect = Rect()
        node.getBoundsInScreen(rect)
        if (rect.width() <= 0 || rect.height() <= 0) {
            QLog.i("Auto", "「$TARGET_TEXT」节点没有有效坐标（$rect），放弃")
            return
        }
        val cx = rect.exactCenterX()
        val cy = rect.exactCenterY()
        val path = Path().apply { moveTo(cx, cy) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0L, TAP_DURATION_MS))
            .build()
        val accepted = dispatchGesture(
            gesture,
            object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    QLog.i("Auto", "手势点击已完成：($cx, $cy)，这一台不再自动点")
                    AutoUnlock.disarm("已自动点击「$TARGET_TEXT」（手势）")
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    QLog.w("Auto", "手势点击被系统取消：($cx, $cy)")
                }
            },
            null
        )
        QLog.i("Auto", "点击「$TARGET_TEXT」用 dispatchGesture 提交=$accepted 坐标=($cx, $cy)")
    }

    /** 一次遍历的扫描结果。三件事一次看完，避免对同一棵树反复 BFS。 */
    private class PageScan {
        var hasDeviceMark = false
        var loadingMark: String? = null
        var target: AccessibilityNodeInfo? = null
        var targetIsExact = false
    }

    /**
     * 扫一遍当前窗口，同时回答三个问题：
     *   1. 这是不是设备页（有没有「出水口」）
     *   2. 是不是还在加载（有没有「内容准备中」之类）
     *   3. 「立即使用」在哪（优先完全相等的文本，退一步用包含匹配）
     *
     * 一次遍历做完，是因为设备页每重绘一帧都会发事件，分开三次 BFS 在
     * 上千个节点上会明显拖慢主线程。
     */
    private fun scanPage(root: AccessibilityNodeInfo): PageScan {
        val result = PageScan()
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        var visited = 0

        while (queue.isNotEmpty() && visited < 1500) {
            val node = queue.removeFirst()
            visited++

            if (node.isVisibleToUser) {
                val text = node.text?.toString()
                val desc = node.contentDescription?.toString()
                val combined = when {
                    text == null -> desc
                    desc == null -> text
                    else -> "$text $desc"
                }

                if (combined != null) {
                    if (!result.hasDeviceMark &&
                        (text == DEVICE_PAGE_MARK || desc == DEVICE_PAGE_MARK ||
                            text?.contains(DEVICE_PAGE_MARK) == true)
                    ) {
                        result.hasDeviceMark = true
                    }

                    if (result.loadingMark == null) {
                        for (mark in LOADING_MARKS) {
                            if (combined.contains(mark)) {
                                result.loadingMark = mark
                                break
                            }
                        }
                    }

                    // 完全相等优先；包含匹配只在还没拿到精确命中时顶上
                    if (text == TARGET_TEXT) {
                        if (!result.targetIsExact) {
                            result.target = node
                            result.targetIsExact = true
                        }
                    } else if (result.target == null &&
                        (text?.contains(TARGET_TEXT) == true || desc?.contains(TARGET_TEXT) == true)
                    ) {
                        result.target = node
                    }
                }
            }

            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return result
    }

    override fun onInterrupt() {
        QLog.i("Auto", "无障碍服务被中断")
    }

    override fun onDestroy() {
        stopPolling()
        QLog.i("Auto", "无障碍服务已销毁")
        super.onDestroy()
    }
}
