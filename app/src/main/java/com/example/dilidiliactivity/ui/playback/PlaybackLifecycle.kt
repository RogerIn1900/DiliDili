package com.example.dilidiliactivity.ui.playback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.dilidiliactivity.domain.playback.PlaybackSession

@Composable
fun PlaybackLifecycle(session: PlaybackSession) {
    val owner = LocalLifecycleOwner.current
    DisposableEffect(owner, session) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> session.start()
                Lifecycle.Event.ON_STOP -> session.stop()
                else -> Unit
            }
        }
        owner.lifecycle.addObserver(observer)
        if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) session.start()
        onDispose { owner.lifecycle.removeObserver(observer); session.stop() }
    }
}
