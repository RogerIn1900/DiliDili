package com.example.dilidiliactivity.data.local.archive

import androidx.room.Entity

@Entity(tableName = "archive_feed_entries", primaryKeys = ["regionId", "bvid"])
data class ArchiveFeedEntry(val regionId: Int, val bvid: String, val position: Int)
