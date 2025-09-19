package com.example.dilidiliactivity.data.local.archive

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ArchiveEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun archiveDao(): ArchiveDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "archive_table"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
