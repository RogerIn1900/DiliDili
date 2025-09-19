package com.example.dilidiliactivity.data.local.VideoPlayerData

data class FansResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: FansData
)

data class FansData(
    val mid: Long,
    val following: Int,
    val whisper: Int,
    val black: Int,
    val follower: Int
)
