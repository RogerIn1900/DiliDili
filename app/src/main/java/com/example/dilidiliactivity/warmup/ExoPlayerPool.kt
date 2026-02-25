package com.example.dilidiliactivity.warmup

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.exoplayer.ExoPlayer
import timber.log.Timber

/**
 * ExoPlayer 实例池，预创建一个实例以消除首次 build 的解码器探测开销。
 * 基线：ExoPlayer.Builder.build() 首次 59-65ms。
 *
 * ExoPlayer 必须在有 Looper 的线程创建，这里 post 到主线程队列。
 */
object ExoPlayerPool {

    @Volatile
    private var warmPlayer: ExoPlayer? = null

    fun warmup(context: Context) {
        Handler(Looper.getMainLooper()).post {
            try {
                val start = System.currentTimeMillis()
                warmPlayer = ExoPlayer.Builder(context.applicationContext).build()
                Timber.d("[Warmup] ExoPlayer pool warmup: %dms", System.currentTimeMillis() - start)
            } catch (e: Exception) {
                Timber.w(e, "[Warmup] ExoPlayer warmup failed")
            }
        }
    }

    /**
     * 获取预热的 ExoPlayer 实例。如果池中有可用实例则返回，否则创建新实例。
     * 调用后池中实例被消耗，后续调用将创建新实例。
     */
    fun acquire(context: Context): ExoPlayer {
        val cached = warmPlayer
        if (cached != null) {
            warmPlayer = null
            Timber.d("[Warmup] ExoPlayer acquired from pool")
            return cached
        }
        Timber.d("[Warmup] ExoPlayer pool empty, creating new instance")
        return ExoPlayer.Builder(context).build()
    }
}
