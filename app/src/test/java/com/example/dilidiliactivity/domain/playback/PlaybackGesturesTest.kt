package com.example.dilidiliactivity.domain.playback

import org.junit.Assert.*
import org.junit.Test

class PlaybackGesturesTest {
    @Test fun `movement inside threshold does not choose a gesture`() {
        assertNull(chooseGesture(2f, 3f, 10f, 100f, 16f))
    }
    @Test fun `dominant direction and initial region choose mode`() {
        assertEquals(PlayerGestureMode.Seek, chooseGesture(40f, 20f, 10f, 100f, 16f))
        assertEquals(PlayerGestureMode.Brightness, chooseGesture(0f, 20f, 10f, 100f, 16f))
        assertEquals(PlayerGestureMode.Volume, chooseGesture(0f, 20f, 90f, 100f, 16f))
    }
    @Test fun `unknown duration and invalid dimensions never seek`() {
        assertNull(seekTargetMs(0, 10f, 100, -1))
        assertNull(seekTargetMs(0, 10f, 0, 100))
        assertNull(seekTargetMs(0, Float.NaN, 100, 100))
    }
    @Test fun `seek clamps at start and end`() {
        assertEquals(0L, seekTargetMs(500, -100f, 100, 10_000))
        assertEquals(10_000L, seekTargetMs(500, 100f, 100, 10_000))
    }
    @Test fun `half screen seeks thirty seconds`() {
        assertEquals(31_000L, seekTargetMs(1_000, 50f, 100, 100_000))
    }
    @Test fun `boost restores original speed even after repeated begin and end`() {
        var speed = 1.5f
        val boost = TemporarySpeedBoost({ speed }, { speed = it })
        boost.begin(); boost.begin()
        assertEquals(3f, speed, 0f)
        boost.end(); boost.end()
        assertEquals(1.5f, speed, 0f)
    }
}
