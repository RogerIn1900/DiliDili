package com.example.dilidiliactivity.ui.Pages.homePage.ZuixinPage

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VideoPlayerViewModel
import com.example.dilidiliactivity.ui.common.myView.VideoTimelineView

@Composable
fun ZuixinPage(
    modifier: Modifier = Modifier,
    durationMs: Long = 10_000L,
    currentPositionMs: Long = 3_000L,
    bufferedRanges: List<Pair<Long, Long>> = emptyList(),
    waveform: FloatArray? = null,
    amplitudes: List<Float> = emptyList(),
    onScrub: (Long) -> Unit = {},
    onSeekReleased: (Long) -> Unit = {},
    viewModel: VideoPlayerViewModel? = null
){
    AndroidView(
        factory = { context ->
            VideoTimelineView(context).apply {
                // 初始化时设置一次
                setOnScrubListener { pos, fromUser ->
                    if (fromUser) {
                        onScrub(pos)
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        update = { view ->
            // Compose → View 单向同步
            view.durationMs = durationMs
            view.setPosition(currentPositionMs)
            view.bufferedRanges = bufferedRanges
            waveform?.let { view.setWaveform(it) }
            if (amplitudes.isNotEmpty()) view.updateAmplitudes(amplitudes)
        }
    )
}


@Preview
@Composable
fun ZuixinPagePreview(){
    ZuixinPage(
        durationMs = 10_000L,
        currentPositionMs = 3_000L,
        bufferedRanges = listOf(0L to 5000L),
        waveform = FloatArray(100) { (0..50).random().toFloat() }
    )
}