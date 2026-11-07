package com.local.qrjump

import android.content.Context
import android.os.SystemClock

/**
 * 「到设备页后自动点一下立即使用」的**触发窗口**控制。
 *
 * ── 为什么需要一个窗口，而不是常开 ──
 * 无障碍服务一旦开启就是全局的。如果它在任何时候看到「立即使用」就去点，
 * 那用户自己手动打开支付宝、自己翻到某台设备页时也会被莫名其妙点掉，
 * 而且可能点的是别的楼栋、别的机器。所以这里做了一个「只在刚唤起后的一小段
 * 时间里生效」的约束：
 *
 *   本应用成功唤起一次设备页  →  [arm] 打一个时间戳
 *   2 分钟内出现官方设备页  →  等够设定秒数后点一次，然后立刻 [disarm]
 *   超过 2 分钟  →  自动失效
 *
 * 这个设计让自动点击的作用域严格等于「我自己刚唤起的这一次」，
 * 而不是「任何一次出现在屏幕上的设备页」。
 *
 * ── 为什么要等（2026-11-06 用户反馈「打开网页后加载有延迟」）──
 * 翻 21:04 的控件树 dump 能证实：
 *
 *   小票余额 40.42元 / 出水口 冷水 热水 / **立即使用** / **内容准备中** / 刷新
 *
 * ——「立即使用」和「内容准备中」是同一次 dump 里同时存在的节点。
 * 也就是说按钮在页面还没准备好的时候就已经在树里了。这时候点下去，
 * 点的是一个没接上设备状态的页面，官方那边多半不理，或者提示要刷新。
 *
 * ── 计时口径（2026-11-06 22:05 改成单一时钟，单位秒）──
 * 之前是两道闸相加：等「内容准备中」消失（最多 8 秒宽限）+ 就绪后再等
 * 稳定期（2.5 秒）。实测总时长 3 + 8 + 2.5 = 11 秒左右，而且**不可预测**：
 * 那个「内容准备中」根本不会消失（它其实是页面上独立的小票/广告位慢区域），
 * 于是每次都恒定吃满 8 秒宽限，再叠加稳定期。
 *
 * 现在改成一条可控的时钟：**从第一次认出设备页起，等满设定秒数就点**。
 * 加载标记只用来打日志，不再参与计时。好处是总等待时间 = 设定值，
 * 用户可以自己按楼里的网络情况调（默认 8 秒，设置页 0–30 秒）。
 *
 * ── 红线在哪 ──
 * 这里点的是支付宝小程序页面上的那个真按钮，走的是支付宝自己的控件和
 * 它自己的业务逻辑。我们不构造订单、不调私有接口、不伪造签名、不改金额，
 * 也不碰登录态 —— 与用户自己用手指点一下的区别只在于「谁点的」。
 * 计费、扣款、出水规则全部仍然由官方页面决定。
 */
object AutoUnlock {

    /** 触发窗口：2 分钟。够慢吞吞地从设备列表走到设备页，又不至于长时间挂着一把枪。 */
    private const val WINDOW_MS = 2 * 60 * 1000L

    /** 用 elapsedRealtime 而不是 System.currentTimeMillis：不受用户改系统时间影响。 */
    @Volatile
    private var armedAt = 0L

    /** 已经自动点过一次就不再点，避免页面抖动导致连点。 */
    @Volatile
    private var alreadyClicked = false

    /** 第一次认出「这像设备页」的时刻。整个等待时间从这里起算。 */
    @Volatile
    private var pageSeenAt = 0L

    /** 上一次打印"还差多久"时用的剩余秒数，用来给日志降噪。 */
    @Volatile
    private var lastRemainingSec = -1L

    /** 上一次打过日志的加载标记，避免同一状态每秒刷一行。 */
    @Volatile
    private var lastLoadingMark: String? = null

    /**
     * arm 时记下的等待秒数。放这里是因为 [noteDevicePageSeen] 拿不到 Context，
     * 而它需要这个值来打一行"开始等 N 秒"的日志。
     */
    @Volatile
    private var armedDelaySec = 0

    /**
     * 成功发起一次唤起后调用。
     *
     * 只有在设置里真的打开了自动解锁时才计时；关着就什么都不做，
     * 保证「默认关闭」是彻底的关闭（服务即使被系统启动也不会动手）。
     */
    fun arm(context: Context) {
        if (!Prefs.autoUnlock(context)) {
            QLog.i("Auto", "自动解锁未开启，不进入待命状态")
            return
        }
        // 每次真要唤起设备页之前，先检查无障碍授权还在不在。
        // MIUI 会把「从最近任务划掉卡片」当成 force-stop 并清空授权，
        // 拿到 WRITE_SECURE_SETTINGS 时这里能自动补回来（见 A11yGuard）。
        A11yGuard.heal(context)
        armedAt = SystemClock.elapsedRealtime()
        alreadyClicked = false
        pageSeenAt = 0L
        lastRemainingSec = -1L
        lastLoadingMark = null
        val sec = Prefs.autoUnlockDelaySec(context)
        armedDelaySec = sec
        QLog.i(
            "Auto",
            "进入待命状态（${WINDOW_MS / 1000} 秒内出现设备页则点一次，" +
                "认出设备页后再等 $sec 秒）"
        )
    }

    fun disarm(reason: String) {
        if (armedAt == 0L) return
        armedAt = 0L
        pageSeenAt = 0L
        QLog.i("Auto", "退出待命状态：$reason")
    }

    /** 当前是否处在可动手的窗口内。 */
    fun isArmed(): Boolean {
        if (armedAt == 0L) return false
        val elapsed = SystemClock.elapsedRealtime() - armedAt
        if (elapsed > WINDOW_MS) {
            QLog.i("Auto", "待命已超时（${elapsed / 1000} 秒），不再动手")
            armedAt = 0L
            pageSeenAt = 0L
            return false
        }
        return true
    }

    /**
     * 记录"这次真的看到设备页了"，并从这里开始计时。
     * 重复调用不会重置起点 —— 否则页面每刷一帧就把等待清零，永远等不完。
     */
    fun noteDevicePageSeen() {
        val now = SystemClock.elapsedRealtime()
        if (pageSeenAt != 0L) return
        pageSeenAt = now
        QLog.i("Auto", "已认出设备页，开始计时（等 $armedDelaySec 秒）")
    }

    /**
     * 页面上还有「内容准备中」之类的标记。
     *
     * 现在它**只用于打日志**，不参与计时：实测这块东西是页面上独立的慢区域
     * （小票/广告位），等它等于永远等。真正决定什么时候点的是 [isSettledEnough]。
     */
    fun noteLoading(mark: String) {
        if (lastLoadingMark == mark) return
        lastLoadingMark = mark
        QLog.i("Auto", "页面还显示「$mark」（它是独立慢区域，不影响计时）")
    }

    /** 从认出设备页到现在过去了多少毫秒。还没认出时返回 0。 */
    fun waitedMs(): Long {
        if (pageSeenAt == 0L) return 0L
        return SystemClock.elapsedRealtime() - pageSeenAt
    }

    /**
     * 从认出设备页起，是否已经等够了 [delaySec] 秒。
     *
     * 没等够时打一条带剩余秒数的日志（同样的剩余值只打一次，避免刷屏）。
     */
    fun isSettledEnough(delaySec: Int): Boolean {
        if (pageSeenAt == 0L) return false
        val waited = waitedMs()
        val threshold = delaySec * 1000L
        if (waited >= threshold) return true
        val remainingSec = (threshold - waited + 999) / 1000
        if (remainingSec != lastRemainingSec) {
            lastRemainingSec = remainingSec
            QLog.i("Auto", "还没等够，还差 $remainingSec 秒（阈值 $delaySec 秒）")
        }
        return false
    }

    /** 只有第一次调用返回 true，用来保证「只点一次」。 */
    fun claimOnce(): Boolean {
        if (alreadyClicked) return false
        alreadyClicked = true
        return true
    }

    /**
     * 还没点过、人也还在窗口里吗。
     *
     * 与 [claimOnce] 的区别：这里**不消耗**名额，只用于"值不值得去遍历控件树"
     * 的前置过滤。设备页每渲染一帧都会发事件，点过之后就该直接短路掉，
     * 不然会一直白白遍历上千个节点。
     */
    fun canStillClaim(): Boolean = !alreadyClicked && armedAt != 0L

    /**
     * 判断无障碍服务当前是否真的在跑。
     *
     * 两条路都试，因为每条在各家 ROM 上都不完全可靠：
     *   1) `AccessibilityManager.getEnabledAccessibilityServiceList()` ——
     *      公开 API，正常机型上最准。
     *   2) `AutoUnlockService.onServiceConnected()` 自己写下的回执标记 ——
     *      最实在的口径：服务真的被系统绑上了才会写。
     * 另外 `Settings.Secure.getString(ENABLED_ACCESSIBILITY_SERVICES)` 不可用：
     * 那个 key 从 Android 4.2 起对普通应用受限，本机实测恒为 null。
     */
    fun isServiceEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE)
            as? android.view.accessibility.AccessibilityManager
        if (am != null) {
            val expected = AutoUnlockService::class.java.name
            val hit = am.getEnabledAccessibilityServiceList(
                android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            ).any { info ->
                val name = info.resolveInfo?.serviceInfo?.name ?: ""
                name == expected || name.endsWith(".$expected")
            }
            if (hit) return true
        }
        // 退一步：服务自己连上时写的回执
        return Prefs.a11yConnected(context)
    }
}
