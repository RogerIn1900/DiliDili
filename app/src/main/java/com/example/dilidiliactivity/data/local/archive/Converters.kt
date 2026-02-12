package com.example.dilidiliactivity.data.local.archive

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter fun fromRights(value: Rights): String = gson.toJson(value)
    @TypeConverter fun toRights(value: String): Rights = gson.fromJson(value, Rights::class.java)

    @TypeConverter fun fromOwner(value: Owner): String = gson.toJson(value)
    @TypeConverter fun toOwner(value: String): Owner = gson.fromJson(value, Owner::class.java)

    @TypeConverter fun fromStat(value: Stat): String = gson.toJson(value)
    @TypeConverter fun toStat(value: String): Stat = gson.fromJson(value, Stat::class.java)

    @TypeConverter fun fromDimension(value: Dimension): String = gson.toJson(value)
    @TypeConverter fun toDimension(value: String): Dimension = gson.fromJson(value, Dimension::class.java)
}
