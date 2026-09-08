package com.example.dilidiliactivity.domain.playback

import org.junit.Assert.*
import org.junit.Test

class PlaybackSessionTest {
    private class Store(var saved: PlaybackBookmark? = null) : BookmarkStore {
        override fun read() = saved
        override fun write(bookmark: PlaybackBookmark) { saved = bookmark }
    }
    private class Engine : PlaybackEngine {
        override var positionMs = 0L
        override var playRequested = true
        var prepares = 0
        var releases = 0
        var uri = ""
        override fun prepare(uri: String, positionMs: Long, play: Boolean) {
            this.uri = uri; this.positionMs = positionMs; playRequested = play; prepares++
        }
        override fun release() { releases++ }
    }
    @Test fun `selection while stopped does not allocate a decoder`() {
        var created = 0
        val session = PlaybackSession({ created++; Engine() }, Store())
        session.select("content://video/1")
        assertEquals(0, created)
        session.start()
        assertEquals(1, created)
    }
    @Test fun `same media and layout restart do not prepare twice`() {
        val engine = Engine()
        val session = PlaybackSession({ engine }, Store())
        session.select("video"); session.start(); session.select("video"); session.start()
        assertEquals(1, engine.prepares)
    }
    @Test fun `background saves position and releases once`() {
        val engine = Engine(); val store = Store()
        val session = PlaybackSession({ engine }, store)
        session.select("video"); session.start(); engine.positionMs = 9_000L
        session.stop(); session.stop()
        assertEquals(9_000L, store.saved!!.positionMs)
        assertEquals(1, engine.releases)
        assertNull(session.engine.value)
    }
    @Test fun `restored selection preserves a user pause`() {
        val engine = Engine()
        PlaybackSession({ engine }, Store(PlaybackBookmark("video", 4_000L, false))).start()
        assertEquals(4_000L, engine.positionMs)
        assertFalse(engine.playRequested)
    }
    @Test fun `new media resets previous progress without creating another engine`() {
        val engine = Engine(); var created = 0
        val session = PlaybackSession({ created++; engine }, Store())
        session.select("first"); session.start(); engine.positionMs = 500L
        session.select("second")
        assertEquals(0L, engine.positionMs)
        assertEquals("second", engine.uri)
        assertEquals(1, created)
    }
    @Test fun `closed session cannot restart or release twice`() {
        val engine = Engine()
        val session = PlaybackSession({ engine }, Store())
        session.select("video"); session.start(); session.close(); session.close(); session.start()
        assertEquals(1, engine.releases)
        assertNull(session.engine.value)
    }
    @Test fun `checkpoint reads actual playback intent`() {
        val engine = Engine(); val store = Store()
        val session = PlaybackSession({ engine }, store)
        session.select("video"); session.start(); engine.playRequested = false; session.checkpoint()
        assertFalse(store.saved!!.play)
    }
}
