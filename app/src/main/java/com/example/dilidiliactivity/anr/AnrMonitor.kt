package com.example.dilidiliactivity.anr

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference

/**
 * ANR 采集：WatchDog 方式。
 * 在后台线程定时向主线程发 tick，若主线程在阈值内未响应则认为可能 ANR，并 dump 主线程栈 + 当前阶段。
 * 可选：与 SIGQUIT 配合时，当前阶段会写入 [stageFile]，便于 native 层或系统 traces 对照。
 */
object AnrMonitor {

    private const val TAG = "AnrMonitor"

    /** 主线程无响应判定阈值（毫秒），建议 3s–5s，与系统 ANR 阈值（通常 5s）接近即可 */
    @Volatile
    var blockThresholdMs: Long = 4000L
        set(value) {
            field = value.coerceAtLeast(1000L)
        }

    /** WatchDog 轮询间隔（毫秒） */
    @Volatile
    var pollIntervalMs: Long = 2000L
        set(value) {
            field = value.coerceAtLeast(500L)
        }

    @Volatile
    var enabled: Boolean = true

    /** 为 true 时，启动阶段会执行 [simulateAnrInAppStart] 模拟 ANR（主线程阻塞 10 秒），用于测试 WatchDog。默认 true。 */
    @Volatile
    var isAnrTest: Boolean = true

    /**
     * 在主线程阻塞 10 秒，用于启动阶段模拟 ANR。仅当 [isAnrTest] 为 true 时应在启动流程中调用。
     */
    fun simulateAnrInAppStart() {
        Thread.sleep(10_000L)
    }

    private val mainHandler = Handler(Looper.getMainLooper())
    private val currentStage = AtomicReference<AnrStage>(AnrStage.IDLE)
    private var stageFile: File? = null
    private var watchThread: Thread? = null
    private var lastTickDone = true
    private val tickLock = Object()

    /**
     * 设置当前阶段（在五个阶段的入口处调用）。
     */
    fun setStage(stage: AnrStage) {
        currentStage.set(stage)
        stageFile?.let { file ->
            try {
                file.writeText("${stage.tag}\n${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Log.w(TAG, "Write stage file failed", e)
            }
        }
    }

    /**
     * 可选：设置用于写入当前阶段的文件（如 getFilesDir()/anr_stage.txt），便于 SIGQUIT 或 adb 对照。
     */
    fun setStageFile(file: File?) {
        stageFile = file
    }

    /**
     * 启动 WatchDog。建议在 Application#onCreate 末尾、或需要开始监控时调用。
     */
    fun start() {
        if (watchThread?.isAlive == true) return
        lastTickDone = true
        watchThread = Thread({
            while (enabled) {
                try {
                    Thread.sleep(pollIntervalMs)
                    if (!enabled) break
                    tick()
                } catch (e: InterruptedException) {
                    break
                } catch (e: Exception) {
                    Log.e(TAG, "WatchDog tick error", e)
                }
            }
        }, "AnrWatchDog").apply {
            isDaemon = true
            start()
        }
        Log.d(TAG, "WatchDog started (threshold=${blockThresholdMs}ms, interval=${pollIntervalMs}ms)")
    }

    /**
     * 停止 WatchDog。
     */
    fun stop() {
        enabled = false
        watchThread = null
    }

    private fun tick() {
        synchronized(tickLock) { lastTickDone = false }
        mainHandler.post {
            synchronized(tickLock) { lastTickDone = true }
        }
        val deadline = System.currentTimeMillis() + blockThresholdMs
        while (System.currentTimeMillis() < deadline && enabled) {
            synchronized(tickLock) {
                if (lastTickDone) return
            }
            Thread.sleep(200)
        }
        if (!enabled) return
        synchronized(tickLock) {
            if (lastTickDone) return
        }
        onPossibleAnr()
    }

    private fun onPossibleAnr() {
        val stage = currentStage.get()
        val mainThread = Looper.getMainLooper().thread
        val stackTrace = mainThread.stackTrace
        val stack = stackTrace.joinToString("\n") { "    $it" }
        val trace = "ANR WatchDog: main thread blocked (stage=${stage.tag})\n$stack"
        Log.e(TAG, trace)
        dumpToFile(stage, stackTrace)
    }

    private fun dumpToFile(stage: AnrStage, stackTrace: Array<StackTraceElement>) {
        try {
            val dir = stageFile?.parentFile ?: return
            if (!dir.exists()) dir.mkdirs()
            val date = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
            val file = File(dir, "anr_watchdog_${stage.tag}_$date.txt")
            file.writeText("stage=${stage.tag}\ntime=${System.currentTimeMillis()}\n\nmain:\n" +
                stackTrace.joinToString("\n") { "  $it" })
            Log.i(TAG, "Dumped to ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Dump file failed", e)
        }
    }

    /** 仅用于测试：获取当前阶段 */
    fun getCurrentStage(): AnrStage = currentStage.get()
}
