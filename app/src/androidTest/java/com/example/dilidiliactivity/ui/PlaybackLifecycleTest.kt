package com.example.dilidiliactivity.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.example.dilidiliactivity.ui.playback.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class PlaybackLifecycleTest {
    @get:Rule val compose = createAndroidComposeRule<PlaybackDemoActivity>()
    private fun model() = ViewModelProvider(compose.activity)[LocalPlaybackViewModel::class.java]

    @Test fun selectedMediaSurvivesActivityRecreationAndPreservesPause() {
        compose.onNodeWithTag("demo_item_0").performClick()
        compose.waitUntil(10_000) { model().session.engine.value != null }
        compose.runOnIdle {
            val engine = model().session.engine.value as Media3PlaybackEngine
            engine.player.pause(); engine.player.seekTo(1_000L)
            model().session.checkpoint()
        }
        compose.activityRule.scenario.recreate()
        compose.waitUntil(10_000) { model().session.engine.value != null }
        compose.runOnIdle {
            val engine = model().session.engine.value as Media3PlaybackEngine
            assertFalse(engine.playRequested)
            assertTrue(engine.positionMs >= 1_000L)
        }
        compose.onNodeWithTag("demo_player").assertExists()
    }
    @Test fun leavingPlayerReleasesDecoderAndReturningReusesSelection() {
        compose.onNodeWithTag("demo_item_0").performClick()
        compose.waitUntil(10_000) { model().session.engine.value != null }
        compose.onNodeWithTag("back_to_list").performClick()
        compose.runOnIdle { assertNull(model().session.engine.value) }
        compose.onNodeWithTag("demo_item_0").performClick()
        compose.waitUntil(10_000) { model().session.engine.value != null }
        compose.onNodeWithTag("demo_player").assertExists()
    }
    @Test fun backgroundStopReleasesAndResumeRestoresPausedPosition() {
        compose.onNodeWithTag("demo_item_0").performClick()
        compose.waitUntil(10_000) { model().session.engine.value != null }
        compose.runOnIdle {
            val engine = model().session.engine.value as Media3PlaybackEngine
            engine.player.pause()
            engine.player.seekTo(1_500L)
        }
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        compose.activityRule.scenario.onActivity { activity ->
            assertNull(ViewModelProvider(activity)[LocalPlaybackViewModel::class.java].session.engine.value)
        }
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        compose.waitUntil(10_000) { model().session.engine.value != null }
        compose.runOnIdle {
            val engine = model().session.engine.value as Media3PlaybackEngine
            assertFalse(engine.playRequested)
            assertTrue(engine.positionMs >= 1_500L)
        }
    }

}
