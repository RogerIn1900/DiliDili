package com.example.dilidiliactivity.data.local.archive

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArchiveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchive(archive: ArchiveEntity)

    @Query("SELECT * FROM archive_table WHERE bvid = :bvid")
    suspend fun getArchive(bvid: String): ArchiveEntity?

    @Query("SELECT * FROM archive_table WHERE bvid = :bvid LIMIT 1")
    suspend fun getArchiveByBvid(bvid: String): ArchiveEntity?

}
