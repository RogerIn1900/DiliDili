package com.example.dilidiliactivity.domain.playback

import kotlin.math.abs

// Product gesture policy: one full screen of horizontal movement seeks one minute.
private const val SEEK_SPAN_MS = 60_000.0
private const val TEMPORARY_SPEED = 3f

enum class PlayerGestureMode { Brightness, Volume, Seek }

fun chooseGesture(dx: Float, dy: Float, startX: Float, width: Float, threshold: Float): PlayerGestureMode? {
    if (width <= 0f || !dx.isFinite() || !dy.isFinite()) return null
    if (abs(dx) <= threshold && abs(dy) <= threshold) return null
    return if (abs(dx) > abs(dy)) PlayerGestureMode.Seek
    else if (startX < width / 2f) PlayerGestureMode.Brightness else PlayerGestureMode.Volume
}

/** Unknown duration cannot be bounded; leave the player unchanged until metadata is ready. */
fun seekTargetMs(startMs: Long, dx: Float, width: Int, durationMs: Long): Long? {
    if (width <= 0 || durationMs <= 0L || !dx.isFinite()) return null
    return (startMs.toDouble() + dx / width * SEEK_SPAN_MS).coerceIn(0.0, durationMs.toDouble()).toLong()
}

class TemporarySpeedBoost(private val read: () -> Float, private val write: (Float) -> Unit) {
    private var original: Float? = null
    fun begin() {
        if (original != null) return
        original = read()
        write(TEMPORARY_SPEED)
    }
    fun end() {
        val previous = original ?: return
        original = null
        write(previous)
    }
}
