package com.example.dilidiliactivity.anr

import android.util.Log
import java.io.File

/**
 * 可选：SIGQUIT 捕获的 JNI 封装。
 * 调用 [install] 后，当系统向进程发送 SIGQUIT 时，会在指定目录下写入 anr_sigquit_received.txt（内容为 "SIGQUIT"）。
 * 需启用 NDK 并编译 app/src/main/cpp 下的 Native 代码；若未编译，[install] 会静默失败。
 */
object AnrSigquitNative {

    private const val TAG = "AnrSigquitNative"

    init {
        try {
            System.loadLibrary("anr_sigquit")
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "anr_sigquit so not found, SIGQUIT capture disabled", e)
        }
    }

    /**
     * 在指定目录下创建 anr_sigquit_received.txt 并注册 SIGQUIT 处理。
     * @param filesDir 目录的绝对路径，通常为 context.filesDir.absolutePath
     * @return 是否注册成功（若未编译 so 会抛 UnsatisfiedLinkError，由调用方 catch 或使用重载）
     */
    @JvmStatic
    external fun installNative(filesDir: String): Boolean

    @JvmStatic
    fun install(filesDir: String): Boolean = try {
        installNative(filesDir)
    } catch (e: UnsatisfiedLinkError) {
        Log.w(TAG, "install failed: so not loaded", e)
        false
    }

    @JvmStatic
    fun install(filesDir: File): Boolean = install(filesDir.absolutePath)
}
