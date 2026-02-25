package com.example.dilidiliactivity.ui.pages.homepage.recommend.video

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.source.MergingMediaSource
import com.example.dilidiliactivity.warmup.ExoPlayerPool
import timber.log.Timber

@UnstableApi
@Composable
fun Media3Player(
    context: Context,
    videoUrl: String,
    audioUrl: String,
    modifier: Modifier = Modifier
) {
    // 创建 ExoPlayer
    val player = remember {
        val start = System.currentTimeMillis()
        ExoPlayerPool.acquire(context).apply {
            val buildCost = System.currentTimeMillis() - start
            Timber.d("[Warmup-Baseline] ExoPlayer.Builder.build (Media3Player): %dms", buildCost)

            val dataSourceFactory = DefaultDataSource.Factory(context)

            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUrl))
            val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(audioUrl))

            val mergedSource = MergingMediaSource(videoSource, audioSource)

            setMediaSource(mergedSource)
            prepare()
            val totalCost = System.currentTimeMillis() - start
            Timber.d("[Warmup-Baseline] ExoPlayer build+prepare (Media3Player): %dms", totalCost)
            playWhenReady = true
        }
    }

    // 确保在 Compose 生命周期结束时释放
    DisposableEffect(player) {
        onDispose { player.release() }
    }

    // 使用 AndroidView 显示 PlayerView
    AndroidView(factory = {
        PlayerView(context).apply {
            this.player = player
        }
    }, modifier = modifier)
}


@UnstableApi
@Composable
fun DashPlayer(videoUrl: String, audioUrl: String) {
    val context = LocalContext.current
    val player = remember {
        val start = System.currentTimeMillis()
        ExoPlayerPool.acquire(context).apply {
            val buildCost = System.currentTimeMillis() - start
            Timber.d("[Warmup-Baseline] ExoPlayer.Builder.build (DashPlayer): %dms", buildCost)

            val videoSource = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                .createMediaSource(MediaItem.fromUri(videoUrl))
            val audioSource = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                .createMediaSource(MediaItem.fromUri(audioUrl))

            val mergedSource = MergingMediaSource(videoSource, audioSource)
            setMediaSource(mergedSource)
            prepare()
            val totalCost = System.currentTimeMillis() - start
            Timber.d("[Warmup-Baseline] ExoPlayer build+prepare (DashPlayer): %dms", totalCost)
            playWhenReady = true
        }
    }

    AndroidView(
        factory = { PlayerView(context).apply { this.player = player } },
        modifier = Modifier.fillMaxSize()
    )
}
