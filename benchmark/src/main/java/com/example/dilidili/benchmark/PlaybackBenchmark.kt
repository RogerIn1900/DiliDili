package com.example.dilidili.benchmark

import android.content.Intent
import androidx.benchmark.macro.*
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val APP_ID = "com.example.dilidiliactivity"
private const val ACTIVITY = "$APP_ID.ui.playback.PlaybackDemoActivity"
// Small smoke baseline; use more iterations on a physical device for comparisons.
private const val ITERATIONS = 3
private const val UI_TIMEOUT_MS = 10_000L
private const val FLINGS_PER_ITERATION = 3
private const val EDGE_MARGIN_DIVISOR = 5

@RunWith(AndroidJUnit4::class)
class PlaybackBenchmark {
    @get:Rule val benchmark = MacrobenchmarkRule()

    @Test fun coldStart() = benchmark.measureRepeated(
        packageName = APP_ID, metrics = listOf(StartupTimingMetric()),
        iterations = ITERATIONS, startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.None(),
        setupBlock = { pressHome() }
    ) { startActivityAndWait(Intent().setClassName(APP_ID, ACTIVITY)) }

    @Test fun listScroll() = benchmark.measureRepeated(
        packageName = APP_ID, metrics = listOf(FrameTimingMetric()),
        iterations = ITERATIONS, compilationMode = CompilationMode.None(),
        setupBlock = {
            killProcess()
            startActivityAndWait(Intent().setClassName(APP_ID, ACTIVITY))
        }
    ) {
        val list = checkNotNull(device.wait(Until.findObject(By.res("demo_list")), UI_TIMEOUT_MS))
        list.setGestureMargin(device.displayWidth / EDGE_MARGIN_DIVISOR)
        repeat(FLINGS_PER_ITERATION) { list.fling(Direction.DOWN); device.waitForIdle() }
    }
}
