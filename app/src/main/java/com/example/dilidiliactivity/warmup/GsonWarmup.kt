package com.example.dilidiliactivity.warmup

import com.example.dilidiliactivity.data.local.archive.Dimension
import com.example.dilidiliactivity.data.local.archive.Owner
import com.example.dilidiliactivity.data.local.archive.Rights
import com.example.dilidiliactivity.data.local.archive.Stat
import com.google.gson.Gson
import timber.log.Timber

/**
 * Gson TypeAdapter 反射缓存预热。
 * 基线：4 个复杂类型首次反序列化合计 3-6ms。
 * 通过 dummy JSON 触发 TypeAdapter 构建，后续反序列化跳过反射开销。
 */
object GsonWarmup {

    /** 共享 Gson 实例，供 Converters 复用 */
    val sharedGson: Gson = Gson()

    fun warmup() {
        try {
            val start = System.currentTimeMillis()
            sharedGson.fromJson("{}", Rights::class.java)
            sharedGson.fromJson("{}", Owner::class.java)
            sharedGson.fromJson("{}", Stat::class.java)
            sharedGson.fromJson("{}", Dimension::class.java)
            Timber.d("[Warmup] Gson TypeAdapter warmup: %dms", System.currentTimeMillis() - start)
        } catch (e: Exception) {
            Timber.w(e, "[Warmup] Gson warmup failed")
        }
    }
}
