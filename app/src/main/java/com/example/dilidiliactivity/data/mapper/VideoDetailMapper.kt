package com.example.dilidiliactivity.data.mapper

import com.example.dilidiliactivity.data.local.DetailsPageData.UserInfo
import com.example.dilidiliactivity.data.local.DetailsPageData.VideoInfo
import com.example.dilidiliactivity.data.local.archive.Archive


// data/mapper/ArchiveMapper.kt
fun Archive.toUserInfo(): UserInfo = UserInfo(
    name = owner.name,
    avatar = owner.face,
    fans = null,
    videoCount = videos
)

fun Archive.toVideoInfo(): VideoInfo {
    return VideoInfo(
        title = this.title,
        description = this.desc,
        views = this.stat.view,
        likes = this.stat.like,
        coins = this.stat.coin,
        favorites = this.stat.favorite,
        shares = this.stat.share
    )
}

fun VideoInfo.toDisplay(): VideoInfoDisplay {
    return VideoInfoDisplay(
        title = title,
        description = description,
        views = formatCount(views),
        likes = formatCount(likes),
        coins = formatCount(coins),
        favorites = formatCount(favorites),
        shares = formatCount(shares)
    )
}

// 用来给 UI 层展示的类型
data class VideoInfoDisplay(
    val title: String,
    val description: String,
    val views: String,
    val likes: String,
    val coins: String,
    val favorites: String,
    val shares: String
)


fun formatCount(count: Int): String {
    return if (count < 10_000) {
        count.toString()
    } else {
        val value = count / 10_000.0
        // 保留 1 位小数，比如 1.5万
        String.format("%.1f万", value)
    }
}

fun Int.toDisplayCount(): String {
    return if (this < 10_000) {
        this.toString()
    } else {
        val value = this / 10_000.0
        String.format("%.1f万", value) // 保留 1 位小数
    }
}


