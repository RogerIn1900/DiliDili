package com.example.dilidiliactivity.data.mapper

import com.example.dilidiliactivity.data.local.VideoPlayerData.VideoIntroUiModel
import com.example.dilidiliactivity.data.local.archive.Archive


fun Archive.toUiModel(): VideoIntroUiModel {
    return VideoIntroUiModel(
        pic = pic,
        period = formatDuration(duration),
        title = title,
        tips = listOf(
            "${stat.like.toDisplayCount()}点赞" to true,
            "${stat.view.toDisplayCount()}播放" to false
        ),
        author = owner.name,
        viewed = "${stat.view.toDisplayCount()}观看",
        time = formatPubDate(pubdate)
    )
}

// 工具函数：秒数转 "mm:ss"
fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val sec = seconds % 60
    return String.format("%02d:%02d", minutes, sec)
}

// 工具函数：发布时间戳转 "x小时前"
fun formatPubDate(pubdate: Long): String {
    val now = System.currentTimeMillis() / 1000
    val diff = now - pubdate
    return when {
        diff < 60 -> "刚刚"
        diff < 3600 -> "${diff / 60}分钟前"
        diff < 86400 -> "${diff / 3600}小时前"
        diff < 2592000 -> "${diff / 86400}天前"
        else -> "很久以前"
    }
}
