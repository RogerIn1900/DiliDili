package com.example.dilidiliactivity.data.local.VideoPlayerData

data class VideoIntroUiModel(
    val pic: String,             // 视频封面
    val period: String,          // 视频时长 (格式化 "mm:ss")
    val title: String,           // 视频标题
    val tips: List<Pair<String, Boolean>>, // 小标签（例如点赞、百万播放）
    val author: String,          // 作者昵称
    val viewed: String,          // 播放量（格式化）
    val time: String             // 发布时间 (如 "3小时前")
)

