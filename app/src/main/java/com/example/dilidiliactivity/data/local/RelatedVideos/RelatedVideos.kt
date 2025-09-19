package com.example.dilidiliactivity.data.local.RelatedVideos

import com.example.dilidiliactivity.data.local.archive.Archive

data class RelatedVideosResponse(
    val code:Int,
    val data:List<Archive>,
    val message:String
)