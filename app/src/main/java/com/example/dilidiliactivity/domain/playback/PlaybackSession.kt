package com.example.dilidiliactivity.domain.playback

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** The engine owns decoder resources; the session owns selection and resume policy. */
interface PlaybackEngine {
    val positionMs: Long
    val playRequested: Boolean
    fun prepare(uri: String, positionMs: Long, play: Boolean)
    fun release()
}

data class PlaybackBookmark(val uri: String, val positionMs: Long = 0L, val play: Boolean = true)

interface BookmarkStore {
    fun read(): PlaybackBookmark?
    fun write(bookmark: PlaybackBookmark)
}

/** Called on the UI thread. No Activity/View references survive a layout change. */
class PlaybackSession(
    private val factory: () -> PlaybackEngine,
    private val bookmarks: BookmarkStore
) {
    private var selection = bookmarks.read()
    private var foreground = false
    private var closed = false
    private val _engine = MutableStateFlow<PlaybackEngine?>(null)
    val engine = _engine.asStateFlow()
    val selectedUri: String? get() = selection?.uri

    fun select(uri: String) {
        require(uri.isNotBlank()) { "Media URI must not be blank" }
        if (closed || selection?.uri == uri) return
        selection = PlaybackBookmark(uri)
        bookmarks.write(selection!!)
        if (foreground) {
            val player = _engine.value ?: factory().also { _engine.value = it }
            player.prepare(uri, 0L, true)
        }
    }

    fun start() {
        if (closed || foreground) return
        foreground = true
        val bookmark = selection ?: return
        val player = factory()
        player.prepare(bookmark.uri, bookmark.positionMs, bookmark.play)
        _engine.value = player
    }

    fun checkpoint() {
        val bookmark = selection ?: return
        val player = _engine.value ?: return
        selection = bookmark.copy(positionMs = player.positionMs.coerceAtLeast(0L), play = player.playRequested)
        bookmarks.write(selection!!)
    }

    fun stop() {
        checkpoint()
        foreground = false
        val player = _engine.value
        _engine.value = null
        player?.release()
    }

    fun close() {
        if (closed) return
        stop()
        closed = true
    }
}
