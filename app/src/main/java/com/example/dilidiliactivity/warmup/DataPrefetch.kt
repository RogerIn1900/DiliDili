package com.example.dilidiliactivity.warmup

import com.example.dilidiliactivity.domain.repository.VideoRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 首页数据预取。
 * 基线：AnimateVideoVM.loadVideos (network+db) 176-288ms。
 * 在 Application 启动后通过后台协程预取首页数据并缓存到数据库，
 * 使用户进入首页时数据已就绪，跳过网络等待。
 */
object DataPrefetch {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DataEntryPoint {
        fun videoRepository(): VideoRepository
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun prefetch(entryPoint: DataEntryPoint) {
        scope.launch {
            try {
                val start = System.currentTimeMillis()
                val repo = entryPoint.videoRepository()
                repo.getVideoList(ps = 10, rid = 1) // 预取首页动画区数据
                Timber.d("[Warmup] Data prefetch done: %dms", System.currentTimeMillis() - start)
            } catch (e: Exception) {
                Timber.w(e, "[Warmup] Data prefetch failed (silent)")
            }
        }
    }
}
