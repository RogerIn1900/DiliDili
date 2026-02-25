package com.example.dilidiliactivity.warmup

import android.content.Context
import com.example.dilidiliactivity.data.local.archive.AppDatabase
import timber.log.Timber

/**
 * Room 数据库预热。
 * 基线：AppDatabase build 10ms + 首次 DAO 查询触发 WAL 初始化。
 * 在后台线程提前触发数据库实例创建和空查询。
 */
object RoomWarmup {

    fun warmup(context: Context) {
        try {
            val start = System.currentTimeMillis()
            val db = AppDatabase.getInstance(context)
            // 执行一次空查询，触发 WAL 初始化和 schema 验证
            db.runInTransaction { }
            Timber.d("[Warmup] Room database warmup: %dms", System.currentTimeMillis() - start)
        } catch (e: Exception) {
            Timber.w(e, "[Warmup] Room warmup failed")
        }
    }
}
