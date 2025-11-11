package com.example.dilidiliactivity.data.local.CommentData

import com.example.dilidiliactivity.data.local.RandomVideoData.Page


data class CommentResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: CommentData,
)

data class CommentData(
    val page: Page
)
