package com.example.dilidiliactivity.data.local.PopularVideoData

data class PopularVidelData(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: Data
)

data class Data(
    val title: String,
    val media_id: String,
    val explain: String,
    val list: List<VideoDetail>
)

data class VideoDetail(
    val aid: Long,
    val videos: Int,
    val tid: Int,
    val tname: String,
    val copyright: Int,
    val pic: String,
    val title: String,
    val pubdate: Long,
    val ctime: Long,
    val desc: String,
    val state: Int,
    val duration: Int,
    val mission_id: Long?,
    val rights: Rights,
    val owner: Owner,
    val stat: Stat,
    val dynamic: String,
    val cid: Long,
    val dimension: Dimension,
    val short_link_v2: String,
    val first_frame: String,
    val pub_location: String,
    val cover43: String?,
    val tidv2: Int?,
    val tnamev2: String?,
    val pid_v2: Int?,
    val pid_name_v2: String?,
    val bvid: String,
    val season_type: Int,
    val is_ogv: Boolean,
    val ogv_info: Any?,   // 可能是复杂对象，这里先用 Any?
    val rcmd_reason: String,
    val enable_vt: Int,
    val ai_rcmd: Any?,
    val achievement: String
)

data class Rights(
    val bp: Int,
    val elec: Int,
    val download: Int,
    val movie: Int,
    val pay: Int,
    val hd5: Int,
    val no_reprint: Int,
    val autoplay: Int,
    val ugc_pay: Int,
    val is_cooperation: Int,
    val ugc_pay_preview: Int,
    val no_background: Int,
    val arc_pay: Int,
    val pay_free_watch: Int
)

data class Owner(
    val mid: Long,
    val name: String,
    val face: String
)

data class Stat(
    val aid: Long,
    val view: Long,
    val danmaku: Long,
    val reply: Long,
    val favorite: Long,
    val coin: Long,
    val share: Long,
    val now_rank: Int,
    val his_rank: Int,
    val like: Long,
    val dislike: Int,
    val vt: Int,
    val vv: Long,
    val fav_g: Int,
    val like_g: Int
)

data class Dimension(
    val width: Int,
    val height: Int,
    val rotate: Int
)


data class ShowInfo(
    val cover: String,
    val duration: Int,
    val title: String,
    val like: Long,
    val view: Long,
    val danmaku: Long,
    val publisher: String,
    val pubdate: Long
)

