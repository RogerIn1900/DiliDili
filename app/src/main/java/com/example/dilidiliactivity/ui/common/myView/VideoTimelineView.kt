package com.example.dilidiliactivity.ui.common.myView

import kotlin.math.roundToLong
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.LruCache
import android.view.MotionEvent
import android.view.View
import android.view.VelocityTracker
import androidx.annotation.MainThread
import androidx.core.content.res.ResourcesCompat
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * VideoTimelineView
 * - 缩略图条（thumbnail atlas）: 使用 Bitmap atlas 缓存多个帧缩略图，避免频繁 decode/alloc
 * - 波形 (waveform): 使用 FloatArray 预计算并缓存，不在 onDraw 中计算
 * - bufferedRanges: 显示已缓冲区间
 * - progress & scrubber: 支持拖动与回调
 *
 * 性能要点在代码注释中已标出（Paint 复用、对象池、invalidate(rect)、LruCache 等）
 */
class VideoTimelineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var amplitudes: List<Float> = emptyList()
    // ----------------------
    // Configuration / state
    // ----------------------
    var durationMs: Long = 0L
        set(value) {
            field = value
            // duration 改变需重新布局/重绘缩略图映射
            recalcThumbnailMapping()
            invalidate()
        }

    // buffered ranges in ms
    var bufferedRanges: List<Pair<Long, Long>> = emptyList()
        set(value) {
            field = value
            // 只重绘缓冲条区域
            invalidate(bufferRect())
        }

    // waveform data: precomputed amplitude [0..1]
    private var waveform: FloatArray? = null

    // thumbnail atlas: key by thumb index
    // 使用 LruCache 避免频繁内存分配
    private val thumbnailCache: LruCache<Int, Bitmap> =
        LruCache((Runtime.getRuntime().maxMemory() / 1024 / 8).toInt())

    // atlas tile size (固定，避免 onDraw 中计算多次)
    private val thumbWidthPx = dpToPx(64)
    private val thumbHeightPx = dpToPx(36)

    // 显示的缩略图数（取决于 view 宽度）
    private var visibleThumbCount = 0

    // mapping: for each visible column, which timestamp it represents
    private var thumbTimestamps: LongArray = LongArray(0)

    // ----------------------
    // Paints & reusable objects (must be created once)
    // ----------------------
    private val paintBg = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#0F0F0F")
    }

    private val paintBuffered = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#80FFFFFF")
    }

    private val paintProgress = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FF1DB954")
    }

    private val paintWave = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#66FFFFFF")
    }

    private val paintThumbBorder = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dpToPx(1).toFloat()
        color = Color.parseColor("#33FFFFFF")
    }

    // reusable objects to avoid allocation in onDraw
    private val rectSrc = Rect()
    private val rectDst = Rect()
    private val tmpRect = Rect()

    // scrubber state
    private var currentPositionMs: Long = 0L
    private var isDragging = false
    private var onScrubListener: ((positionMs: Long, fromUser: Boolean) -> Unit)? = null
    private var velocityTracker: VelocityTracker? = null

    // cached bounds for waveform / thumbnail / buffer regions
    private var waveformTop = 0
    private var waveformBottom = 0
    private var thumbTop = 0
    private var thumbBottom = 0

    // ----------------------
    // Public API
    // ----------------------
    fun setWaveform(data: FloatArray) {
        // 波形应在后台线程预计算完成后传入
        waveform = data
        invalidate(waveformRect())
    }

    fun setThumbnailProvider(provider: (timestampMs: Long) -> Bitmap?) {
        // 你可以实现异步加载策略，把缩略图放入 thumbnailCache
        // provider 由外部（Repository）在线程池中执行 decode/resize，然后缓存
        this.thumbnailProvider = provider
        // 重新映射时间戳
        recalcThumbnailMapping()
        invalidate()
    }

    private var thumbnailProvider: ((Long) -> Bitmap?)? = null

    fun setPosition(positionMs: Long, fromUser: Boolean = false) {
        currentPositionMs = positionMs
        if (!fromUser) {
            // 非用户触发，仅更新小范围
            invalidate(progressRect())
        }
    }

    fun setOnScrubListener(listener: (Long, Boolean) -> Unit) {
        onScrubListener = listener
    }

    // ----------------------
    // Lifecycle / measurement
    // ----------------------
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 固定高度，宽度 match_parent
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = (thumbHeightPx + dpToPx(24)) // thumb + waveform/spacing
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // 计算并缓存布局区域
        waveformTop = dpToPx(2)
        waveformBottom = waveformTop + dpToPx(12)
        thumbTop = waveformBottom + dpToPx(6)
        thumbBottom = thumbTop + thumbHeightPx

        // 计算可见缩略图数
        visibleThumbCount = (w / thumbWidthPx).coerceAtLeast(1)
        thumbTimestamps = LongArray(visibleThumbCount)
        recalcThumbnailMapping()
    }

    private fun recalcThumbnailMapping() {
        if (durationMs <= 0L || visibleThumbCount == 0) return
        val step = durationMs.toDouble() / visibleThumbCount
        for (i in thumbTimestamps.indices) {
            thumbTimestamps[i] = (i * step).roundToLong()
        }
    }

    /** ✅ 提供给 Compose 调用的接口，用于更新音频波形数据 */
    fun updateAmplitudes(newAmplitudes: List<Float>) {
        amplitudes = newAmplitudes
        invalidate() // 重新绘制
    }

    // ----------------------
    // Drawing: MUST be fast
    // ----------------------
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1) background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBg)

        // 2) waveform (static-ish)
        waveform?.let { wv ->
            drawWaveform(canvas, wv)
        }

        // 3) thumbnails strip (draw from cache or placeholder)
        drawThumbnailStrip(canvas)

        // 4) buffered ranges (draw above thumbnails)
        drawBufferedRanges(canvas)

        // 5) progress and scrubber
        drawProgress(canvas)
    }

    // Draw waveform (optimized)
    private fun drawWaveform(canvas: Canvas, wv: FloatArray) {
        // Avoid allocating path each frame - reuse a Path if waveform is dynamic but allocated once.
        // For demonstration we do a simple filled rect-based waveform
        val left = paddingLeft
        val right = width - paddingRight
        val available = right - left
        val n = wv.size
        if (n == 0) return
        val step = available.toFloat() / n
        val base = waveformTop.toFloat()
        val heightRange = (waveformBottom - waveformTop).toFloat()

        // Use a small temp Rect to batch draw
        for (i in 0 until n) {
            val amp = (wv[i].coerceIn(0f, 1f))
            val h = amp * heightRange
            val cx = left + i * step
            rectDst.set(cx.toInt(), (base + (heightRange - h)).toInt(), (cx + step).toInt(), (base + heightRange).toInt())
            // drawRect with Paint (fast)
            canvas.drawRect(rectDst, paintWave)
        }
    }

    // Draw thumbnail strip: use cached bitmaps from LruCache (avoid decode during onDraw)
    private fun drawThumbnailStrip(canvas: Canvas) {
        val left = paddingLeft
        val top = thumbTop
        val count = visibleThumbCount
        val w = thumbWidthPx
        val h = thumbHeightPx

        for (i in 0 until count) {
            val x = left + i * w
            val key = i
            val bmp = thumbnailCache.get(key) ?: run {
                // Placeholder: drawing a rounded rect placeholder; actual decode should be async
                tmpRect.set(x, top, x + w, top + h)
                canvas.drawRoundRect(RectF(tmpRect), 6f, 6f, paintBuffered) // placeholder visual
                continue
            }
            // drawBitmap with src/dst rects (avoid creating new Rects)
            rectSrc.set(0, 0, bmp.width, bmp.height)
            rectDst.set(x, top, x + w, top + h)
            canvas.drawBitmap(bmp, rectSrc, rectDst, null)
            // border
            canvas.drawRect(rectDst, paintThumbBorder)
        }
    }

    // Draw buffered ranges (convert ms -> x)
    private fun drawBufferedRanges(canvas: Canvas) {
        if (durationMs <= 0L) return
        val bufferTop = thumbTop
        val bufferBottom = thumbBottom
        val left = 0f
        val right = width.toFloat()
        val scale = width.toFloat() / durationMs.toFloat()
        for ((s, e) in bufferedRanges) {
            val l = (s * scale).coerceIn(0f, right)
            val r = (e * scale).coerceIn(0f, right)
            canvas.drawRect(l, bufferTop.toFloat(), r, bufferBottom.toFloat(), paintBuffered)
        }
    }

    // Draw progress bar and scrubber
    private fun drawProgress(canvas: Canvas) {
        if (durationMs <= 0L) return
        val x = posToX(currentPositionMs)
        // progress line
        canvas.drawRect(0f, (thumbBottom - dpToPx(4)).toFloat(), x, thumbBottom.toFloat(), paintProgress)
        // scrubber circle
        val radius = dpToPx(8).toFloat()
        canvas.drawCircle(x, (thumbTop + thumbHeightPx / 2).toFloat(), radius, paintProgress)
    }

    // ----------------------
    // Touch handling: drag scrubber
    // ----------------------
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                velocityTracker = VelocityTracker.obtain()
                velocityTracker?.addMovement(event)
                isDragging = true
                handleScrubTo(event.x)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(event)
                handleScrubTo(event.x)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                velocityTracker?.addMovement(event)
                velocityTracker?.computeCurrentVelocity(1000)
                isDragging = false
                velocityTracker?.recycle()
                velocityTracker = null
                onScrubListener?.invoke(currentPositionMs, true)
                parent.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleScrubTo(x: Float) {
        val pos = xToPos(x)
        currentPositionMs = pos
        // Only invalidate the progress region to minimize redraw
        invalidate(progressRect())
        // notify in real-time (fromUser = true)
        onScrubListener?.invoke(pos, true)
    }

    // ----------------------
    // Helpers
    // ----------------------
    private fun xToPos(x: Float): Long {
        val clamped = x.coerceIn(0f, width.toFloat())
        val ratio = clamped / width.toFloat()
        return (ratio * durationMs).toLong()
    }

    private fun posToX(posMs: Long): Float {
        return (posMs.toFloat() / durationMs.toFloat()) * width.toFloat()
    }

    private fun progressRect(): Rect {
        tmpRect.set(0, thumbBottom - dpToPx(8), width, thumbBottom)
        return Rect(tmpRect)
    }

    private fun bufferRect(): Rect {
        tmpRect.set(0, thumbTop, width, thumbBottom)
        return Rect(tmpRect)
    }

    private fun waveformRect(): Rect {
        tmpRect.set(0, waveformTop, width, waveformBottom)
        return Rect(tmpRect)
    }

    // ----------------------
    // Thumbnail caching API (example functions the caller can use)
    // Caller should produce bitmaps async and put into cache
    // ----------------------
    fun putThumbnail(index: Int, bmp: Bitmap) {
        // ensure bitmap size match or scaled
        thumbnailCache.put(index, bmp)
        // only invalidate the atlas region for this tile
        val left = paddingLeft + index * thumbWidthPx
        invalidate(Rect(left, thumbTop, left + thumbWidthPx, thumbBottom))
    }

    // ---------------
    // Utils
    // ---------------
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).roundToInt()
    }

    private fun Long.roundToLong(): Long = this
}
