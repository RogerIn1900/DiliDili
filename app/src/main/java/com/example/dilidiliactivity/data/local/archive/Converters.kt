package com.example.dilidiliactivity.data.local.archive

import androidx.room.TypeConverter
import com.google.gson.Gson
import timber.log.Timber

class Converters {
    private val gson = Gson()

    // 记录每个类型首次反序列化的耗时
    private val firstDeserialize = mutableSetOf<String>()

    @TypeConverter fun fromRights(value: Rights): String = gson.toJson(value)
    @TypeConverter fun toRights(value: String): Rights {
        val start = if ("Rights" !in firstDeserialize) System.currentTimeMillis() else 0L
        val result = gson.fromJson(value, Rights::class.java)
        if ("Rights" !in firstDeserialize) {
            Timber.d("[Warmup-Baseline] Gson first deserialize Rights: %dms", System.currentTimeMillis() - start)
            firstDeserialize.add("Rights")
        }
        return result
    }

    @TypeConverter fun fromOwner(value: Owner): String = gson.toJson(value)
    @TypeConverter fun toOwner(value: String): Owner {
        val start = if ("Owner" !in firstDeserialize) System.currentTimeMillis() else 0L
        val result = gson.fromJson(value, Owner::class.java)
        if ("Owner" !in firstDeserialize) {
            Timber.d("[Warmup-Baseline] Gson first deserialize Owner: %dms", System.currentTimeMillis() - start)
            firstDeserialize.add("Owner")
        }
        return result
    }

    @TypeConverter fun fromStat(value: Stat): String = gson.toJson(value)
    @TypeConverter fun toStat(value: String): Stat {
        val start = if ("Stat" !in firstDeserialize) System.currentTimeMillis() else 0L
        val result = gson.fromJson(value, Stat::class.java)
        if ("Stat" !in firstDeserialize) {
            Timber.d("[Warmup-Baseline] Gson first deserialize Stat: %dms", System.currentTimeMillis() - start)
            firstDeserialize.add("Stat")
        }
        return result
    }

    @TypeConverter fun fromDimension(value: Dimension): String = gson.toJson(value)
    @TypeConverter fun toDimension(value: String): Dimension {
        val start = if ("Dimension" !in firstDeserialize) System.currentTimeMillis() else 0L
        val result = gson.fromJson(value, Dimension::class.java)
        if ("Dimension" !in firstDeserialize) {
            Timber.d("[Warmup-Baseline] Gson first deserialize Dimension: %dms", System.currentTimeMillis() - start)
            firstDeserialize.add("Dimension")
        }
        return result
    }
}
