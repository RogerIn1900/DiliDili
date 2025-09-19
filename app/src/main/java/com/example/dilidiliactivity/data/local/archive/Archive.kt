package com.example.dilidiliactivity.data.local.archive


data class Archive(
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
    val mission_id: Int?,
    val rights: Rights,
    val owner: Owner,
    val stat: Stat,
    val dynamic: String?,
    val cid: Long,
    val dimension: Dimension,
    val short_link_v2: String,
    val first_frame: String,
    val pub_location: String?,
    val cover43: String?,
    val tidv2: Int,
    val tnamev2: String,
    val pid_v2: Int,
    val pid_name_v2: String,
    val bvid: String,
    val season_type: Int,
    val is_ogv: Boolean,
    val ogv_info: Any?,
    val rcmd_reason: String?,
    val enable_vt: Int,
    val ai_rcmd: Any?
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
    val pay_free_watch: Int,
    val i: Int
)

data class Owner(
    val mid: Long,
    val name: String,
    val face: String
)

data class Stat(
    val aid: Long,
    val view: Int,
    val danmaku: Int,
    val reply: Int,
    val favorite: Int,
    val coin: Int,
    val share: Int,
    val now_rank: Int,
    val his_rank: Int,
    val like: Int,
    val dislike: Int,
    val vt: Int,
    val vv: Int,
    val fav_g: Int,
    val like_g: Int
)

data class Dimension(
    val width: Int,
    val height: Int,
    val rotate: Int
)
