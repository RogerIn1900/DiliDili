package com.example.dilidiliactivity.ui.playback

import androidx.lifecycle.SavedStateHandle
import com.example.dilidiliactivity.domain.playback.PlaybackBookmark
import org.junit.Assert.*
import org.junit.Test

class SavedPlaybackBookmarkTest {
    @Test fun `bookmark survives a new store over restored saved state`() {
        val state = SavedStateHandle()
        val bookmark = PlaybackBookmark("content://media/video/12", 8_000L, false)
        SavedPlaybackBookmark(state).write(bookmark)
        val restored = SavedStateHandle(state.keys().associateWith { state.get<Any>(it) })
        assertEquals(bookmark, SavedPlaybackBookmark(restored).read())
    }
    @Test fun `fresh state has no selected video`() {
        assertNull(SavedPlaybackBookmark(SavedStateHandle()).read())
    }
}
