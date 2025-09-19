package com.example.dilidiliactivity.data.local.RandomVideoData

import com.example.dilidiliactivity.data.local.archive.Archive

// 1. 数据类定义

data class DynamicRegionResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: Data
)

data class Data(
    val page: Page,
    val archives: List<Archive>
)

data class Page(
    val num: Int,
    val size: Int,
    val count: Int
)
