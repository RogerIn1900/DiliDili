package com.example.dilidiliactivity.ui.playback

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.dilidiliactivity.domain.playback.BookmarkStore
import com.example.dilidiliactivity.domain.playback.PlaybackBookmark
import com.example.dilidiliactivity.domain.playback.PlaybackEngine
import com.example.dilidiliactivity.domain.playback.PlaybackSession
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MEDIA_URI_KEY = "playback.uri"
private const val POSITION_MS_KEY = "playback.positionMs"
private const val PLAY_REQUESTED_KEY = "playback.playRequested"
// Checkpoint every second while visible; also save synchronously before releasing on stop.
private const val CHECKPOINT_INTERVAL_MS = 1_000L

class SavedPlaybackBookmark(private val state: SavedStateHandle) : BookmarkStore {
    override fun read(): PlaybackBookmark? = state.get<String>(MEDIA_URI_KEY)?.let {
        PlaybackBookmark(it, state[POSITION_MS_KEY] ?: 0L, state[PLAY_REQUESTED_KEY] ?: true)
    }
    override fun write(bookmark: PlaybackBookmark) {
        state[MEDIA_URI_KEY] = bookmark.uri
        state[POSITION_MS_KEY] = bookmark.positionMs
        state[PLAY_REQUESTED_KEY] = bookmark.play
    }
}

class Media3PlaybackEngine(context: Context, onError: (String) -> Unit) : PlaybackEngine {
    private var prepareStartedMs: Long? = null
    val player: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        addListener(object : Player.Listener {
            override fun onRenderedFirstFrame() {
                prepareStartedMs?.let { started ->
                    android.util.Log.i("PlaybackMetrics", "first_frame_ms=${android.os.SystemClock.elapsedRealtime() - started}")
                    prepareStartedMs = null
                }
            }
            override fun onPlayerError(error: PlaybackException) = onError(error.errorCodeName)
        })
    }
    override val positionMs: Long get() = player.currentPosition
    override val playRequested: Boolean get() = player.playWhenReady
    override fun prepare(uri: String, positionMs: Long, play: Boolean) {
        prepareStartedMs = android.os.SystemClock.elapsedRealtime()
        player.setMediaItem(MediaItem.fromUri(uri), positionMs)
        player.playWhenReady = play
        player.prepare()
    }
    override fun release() = player.release()
}

@HiltViewModel
class LocalPlaybackViewModel @Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    val session = PlaybackSession(
        factory = { Media3PlaybackEngine(context) { _error.value = "播放失败：$it，请重新选择视频" } },
        bookmarks = SavedPlaybackBookmark(savedStateHandle)
    )
    init {
        viewModelScope.launch {
            while (isActive) {
                delay(CHECKPOINT_INTERVAL_MS)
                session.checkpoint()
            }
        }
    }
    fun select(uri: String) {
        _error.value = null
        session.select(uri)
    }
    override fun onCleared() = session.close()
}
