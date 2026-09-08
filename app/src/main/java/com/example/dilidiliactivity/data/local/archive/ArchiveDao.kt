package com.example.dilidiliactivity.data.local.archive

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ArchiveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchive(archive: ArchiveEntity)

    @Query("SELECT * FROM archive_table WHERE bvid = :bvid")
    suspend fun getArchive(bvid: String): ArchiveEntity?

    @Query("SELECT * FROM archive_table WHERE bvid = :bvid LIMIT 1")
    suspend fun getArchiveByBvid(bvid: String): ArchiveEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchives(archives: List<ArchiveEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<ArchiveFeedEntry>)

    @Query("DELETE FROM archive_feed_entries WHERE regionId = :regionId")
    suspend fun clearRegion(regionId: Int)

    @Query("SELECT a.* FROM archive_table a INNER JOIN archive_feed_entries f ON a.bvid = f.bvid WHERE f.regionId = :regionId ORDER BY f.position")
    fun observeRegion(regionId: Int): Flow<List<ArchiveEntity>>

    @Query("SELECT a.* FROM archive_table a INNER JOIN archive_feed_entries f ON a.bvid = f.bvid WHERE f.regionId = :regionId ORDER BY f.position")
    suspend fun getRegion(regionId: Int): List<ArchiveEntity>

    @Transaction
    suspend fun replaceRegion(regionId: Int, archives: List<ArchiveEntity>) {
        val unique = archives.distinctBy { it.bvid }
        insertArchives(unique)
        clearRegion(regionId)
        insertEntries(unique.mapIndexed { index, item -> ArchiveFeedEntry(regionId, item.bvid, index) })
    }
}
