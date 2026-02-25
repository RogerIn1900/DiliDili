package com.example.dilidiliactivity.warmup

import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import com.example.dilidiliactivity.data.remote.api.PopularVideoApi
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import timber.log.Timber

/**
 * OkHttpClient + Retrofit + API 代理预热。
 * 基线：OkHttpClient 28-30ms, Retrofit 4-5ms, API 代理 0-1ms，合计 ~33ms。
 * 通过 Hilt EntryPoint 在后台线程触发 Singleton 创建，将耗时从用户交互路径上移除。
 */
object NetworkWarmup {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NetworkEntryPoint {
        fun okHttpClient(): OkHttpClient
        fun retrofit(): Retrofit
        fun bilibiliApi(): BilibiliApi
        fun popularVideoApi(): PopularVideoApi
    }

    fun warmup(entryPoint: NetworkEntryPoint) {
        try {
            val start = System.currentTimeMillis()
            entryPoint.okHttpClient()
            entryPoint.retrofit()
            entryPoint.bilibiliApi()
            entryPoint.popularVideoApi()
            Timber.d("[Warmup] Network layer warmup: %dms", System.currentTimeMillis() - start)
        } catch (e: Exception) {
            Timber.w(e, "[Warmup] Network warmup failed")
        }
    }
}
