package com.local.qrjump

import android.util.Log

/**
 * 全链路日志。所有输出统一用 TAG = QrJump，方便一条 adb 命令抓全。
 *
 * 分区（area）约定：
 *   Life    —— Activity 生命周期、权限
 *   Scan    —— 相机、解码器、原始扫码内容
 *   Parse   —— URL 校验的每一步结论
 *   Launch  —— 策略选择、resolveActivity、最终 Intent
 *   Web     —— 策略 C 的 WebView 内部事件
 *
 * 过滤命令： adb logcat -s QrJump
 */
object QLog {

    const val TAG = "QrJump"

    fun i(area: String, msg: String) = Log.i(TAG, "[$area] $msg")

    fun w(area: String, msg: String) = Log.w(TAG, "[$area] $msg")

    fun e(area: String, msg: String, tr: Throwable? = null) =
        Log.e(TAG, "[$area] $msg", tr)

    /** 异常统一格式化：类型 + message + 首帧栈，够定位又不刷屏。 */
    fun describe(t: Throwable): String {
        val head = t.stackTrace.firstOrNull()?.let { " @${it.className}.${it.methodName}:${it.lineNumber}" } ?: ""
        return "${t.javaClass.simpleName}: ${t.message}$head"
    }
}
