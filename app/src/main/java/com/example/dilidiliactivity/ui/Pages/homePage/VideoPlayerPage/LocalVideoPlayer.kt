package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import com.example.dilidiliactivity.R
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
@Composable
fun  LocalVideoPlayer(modifier: Modifier = Modifier)  {
    val context = LocalContext.current

    // 初始化 ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // 从 res/raw 加载视频
            val uri = Uri.parse("android.resource://${context.packageName}/${R.raw.video}")
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    // 在 Compose 中嵌入 PlayerView
    AndroidView(
    modifier = modifier,
    factory = {
        PlayerView(context).apply {
            player = exoPlayer
            useController = true // 显示控制条
        }
    }
    )
}