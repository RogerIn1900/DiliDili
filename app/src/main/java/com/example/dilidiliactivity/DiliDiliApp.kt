package com.example.dilidiliactivity

import android.app.Application
import com.example.dilidiliactivity.anr.AnrMonitor
import com.example.dilidiliactivity.anr.AnrStage
import com.example.dilidiliactivity.anr.AnrSigquitNative
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.io.File

@HiltAndroidApp
class DiliDiliApp : Application() {
    override fun onCreate() {
        AnrMonitor.setStage(AnrStage.APP_START)
        if (AnrMonitor.isAnrTest) {
            AnrMonitor.simulateAnrInAppStart()
        }
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        val filesDir = filesDir
        AnrMonitor.setStageFile(File(filesDir, "anr_stage.txt"))
        AnrMonitor.start()
        AnrSigquitNative.install(filesDir)
        AnrMonitor.setStage(AnrStage.IDLE)
    }
}
