package com.example.dilidiliactivity.data.local.DetailsPageData

// 第一个类型：用户信息
data class UserInfo(
    val name: String,
    val avatar: String,
    val fans: Int?,   // 这里Archive里没有粉丝数，可以置null或者额外API获取
    val videoCount: Int
)

// 第二个类型：视频信息
data class VideoInfo(
    val title: String,
    val description: String,
    val views: Int,
    val likes: Int,
    val coins: Int,
    val favorites: Int,
    val shares: Int
)