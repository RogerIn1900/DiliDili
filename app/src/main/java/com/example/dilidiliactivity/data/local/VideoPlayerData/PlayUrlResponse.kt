package com.example.dilidiliactivity.data.local.VideoPlayerData

data class PlayUrlResponse(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: PlayUrlData
)

data class PlayUrlData(
    val from: String,
    val result: String,
    val message: String,
    val quality: Int,
    val format: String,
    val timelength: Long,
    val accept_format: String,
    val accept_description: List<String>,
    val accept_quality: List<Int>,
    val video_codecid: Int,
    val seek_param: String,
    val seek_type: String,
    val durl: List<PlayUrlDurl>,
    val support_formats: List<PlayUrlSupportFormat>,
    val high_format: String?,  // 可能为 null
    val last_play_time: Long,
    val last_play_cid: Long,
    val view_info: String?,    // 可能为 null
    val play_conf: PlayUrlPlayConf
)

data class PlayUrlDurl(
    val order: Int,
    val length: Long,
    val size: Long,
    val ahead: String,
    val vhead: String,
    val url: String,
    val backup_url: List<String>
)

data class PlayUrlSupportFormat(
    val quality: Int,
    val format: String,
    val new_description: String,
    val display_desc: String,
    val superscript: String,
    val codecs: String?
)

data class PlayUrlPlayConf(
    val is_new_description: Boolean
)
