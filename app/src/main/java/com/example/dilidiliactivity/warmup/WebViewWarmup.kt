package com.example.dilidiliactivity.warmup

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import timber.log.Timber

/**
 * WebView 引擎预热。
 * 首次创建 WebView 需要加载 Chromium 内核（实测 253ms），后续创建仅需 ~12ms。
 * 在 Application 启动后 post 到主线程队列，提前触发引擎加载。
 */
object WebViewWarmup {

    @Volatile
    private var warmed = false

    fun warmup(context: Context) {
        if (warmed) return
        Handler(Looper.getMainLooper()).post {
            try {
                val start = System.currentTimeMillis()
                val dummy = WebView(context.applicationContext)
                dummy.destroy()
                warmed = true
                Timber.d("[Warmup] WebView engine warmup: %dms", System.currentTimeMillis() - start)
            } catch (e: Exception) {
                Timber.w(e, "[Warmup] WebView warmup failed")
            }
        }
    }
}
