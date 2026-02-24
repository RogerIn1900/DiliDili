package com.example.dilidiliactivity.data.local.archive

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import timber.log.Timber

@Database(entities = [ArchiveEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun archiveDao(): ArchiveDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val start = System.currentTimeMillis()
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "archive_table"
                ).fallbackToDestructiveMigration()
                    .build().also {
                        INSTANCE = it
                        Timber.d("[Warmup-Baseline] Room AppDatabase build: %dms", System.currentTimeMillis() - start)
                    }
            }
        }
    }
}
