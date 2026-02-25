package com.example.dilidiliactivity

import android.app.Application
import com.example.dilidiliactivity.warmup.AppWarmup
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

        // 启动所有预热任务（WebView、ExoPlayer、Room、网络层、Gson、首页数据预取）
        AppWarmup.start(this)
    }
}
