package com.example.dilidiliactivity.data.local.MediaResponse

import com.example.dilidiliactivity.data.local.archive.Archive

data class MediaResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: MediaData
)

data class MediaData(
    val title: String,
    val media_id: Long,
    val explain: String,
    val list: List<Archive>
)