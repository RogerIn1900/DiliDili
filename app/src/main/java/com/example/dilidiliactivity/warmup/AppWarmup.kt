package com.example.dilidiliactivity.warmup

import android.content.Context
import dagger.hilt.android.EntryPointAccessors
import timber.log.Timber
import kotlin.concurrent.thread

/**
 * 统一预热调度器，在 Application.onCreate() 中调用。
 *
 * 预热时序：
 * - [主线程 post] WebView 引擎预热（已有）
 * - [主线程 post] ExoPlayer 实例预热
 * - [后台线程 A] Room 数据库 + Gson TypeAdapter（串行，共用线程）
 * - [后台线程 B] OkHttpClient + Retrofit + API 代理 → 首页数据预取（串行依赖）
 *
 * 后台线程 A/B 并行执行，不阻塞主线程。
 */
object AppWarmup {

    fun start(context: Context) {
        val start = System.currentTimeMillis()

        // [主线程 post] P0: WebView 引擎预热（已有）
        WebViewWarmup.warmup(context)

        // [主线程 post] P1: ExoPlayer 实例预热
        ExoPlayerPool.warmup(context)

        // [后台线程 A] P0: Room 数据库 + P2: Gson TypeAdapter
        thread(name = "Warmup-Infra", isDaemon = true) {
            RoomWarmup.warmup(context)
            GsonWarmup.warmup()
        }

        // [后台线程 B] P1: 网络层 → P3: 首页数据预取
        thread(name = "Warmup-Network", isDaemon = true) {
            val networkEntryPoint = EntryPointAccessors.fromApplication(
                context, NetworkWarmup.NetworkEntryPoint::class.java
            )
            NetworkWarmup.warmup(networkEntryPoint)

            // 网络层就绪后立即预取首页数据
            val dataEntryPoint = EntryPointAccessors.fromApplication(
                context, DataPrefetch.DataEntryPoint::class.java
            )
            DataPrefetch.prefetch(dataEntryPoint)
        }

        Timber.d("[Warmup] AppWarmup scheduled in: %dms", System.currentTimeMillis() - start)
    }
}
