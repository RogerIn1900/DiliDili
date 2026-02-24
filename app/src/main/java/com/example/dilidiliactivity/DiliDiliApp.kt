package com.example.dilidiliactivity

import android.app.Application
import com.example.dilidiliactivity.warmup.WebViewWarmup
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class DiliDiliApp : Application() {

    companion object {
        /** Application.onCreate 开始的时间戳，供后续打点计算相对耗时 */
        var appCreateTimestamp: Long = 0L
    }

    override fun onCreate() {
        appCreateTimestamp = System.currentTimeMillis()
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.d("[Warmup-Baseline] Application.onCreate done: %dms", System.currentTimeMillis() - appCreateTimestamp)

        // P0: WebView 引擎预热（post 到主线程队列，不阻塞 onCreate）
        WebViewWarmup.warmup(this)
    }
}
