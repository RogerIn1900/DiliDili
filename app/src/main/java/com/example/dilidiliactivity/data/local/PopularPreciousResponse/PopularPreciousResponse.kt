package com.example.dilidiliactivity.data.local.PopularPreciousResponse

import com.example.dilidiliactivity.data.local.archive.Archive

data class PopularPreciousResponse(
    val code:Int,
    val message: String,
    val ttl: Int,
    val data: RuData
)

data class RuData(
    val title:String,
    val media_id:String,
    val explain: String,
    val list: List<Archive>
)