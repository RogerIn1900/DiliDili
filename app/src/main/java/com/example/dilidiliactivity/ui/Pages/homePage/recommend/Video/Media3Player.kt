package com.example.dilidiliactivity.ui.Pages.homePage.recommend.Video

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
        ExoPlayer.Builder(context).build().apply {
            val dataSourceFactory = DefaultDataSource.Factory(context)

            val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(videoUrl))
            val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(audioUrl))

            val mergedSource = MergingMediaSource(videoSource, audioSource)

            setMediaSource(mergedSource)
            prepare()
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
        ExoPlayer.Builder(context).build().apply {
            val videoSource = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                .createMediaSource(MediaItem.fromUri(videoUrl))
            val audioSource = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                .createMediaSource(MediaItem.fromUri(audioUrl))

            val mergedSource = MergingMediaSource(videoSource, audioSource)
            setMediaSource(mergedSource)
            prepare()
            playWhenReady = true
        }
    }

    AndroidView(
        factory = { PlayerView(context).apply { this.player = player } },
        modifier = Modifier.fillMaxSize()
    )
}
