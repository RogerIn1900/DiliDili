package com.example.dilidiliactivity.data.local.RandomVideoData
import kotlinx.serialization.Serializable

@Serializable
data class VideoInfo(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: VideoData? = null,   // 注意可能为空
    val session: String? = null
)

@Serializable
data class VideoData(
    val dash: DashInfo? = null,
    val support_formats: List<SupportFormat>? = null
)

@Serializable
data class DashInfo(
    val video: List<VideoStream>? = null,
    val audio: List<AudioStream>? = null
)

@Serializable
data class VideoStream(
    val id: Int,
    val baseUrl: String,
    val backupUrl: List<String>? = null,
    val bandwidth: Int,
    val mimeType: String,
    val codecs: String,
    val width: Int,
    val height: Int,
    val frameRate: String
)

@Serializable
data class AudioStream(
    val id: Int,
    val baseUrl: String,
    val backupUrl: List<String>? = null,
    val bandwidth: Int,
    val mimeType: String,
    val codecs: String
)

@Serializable
data class SupportFormat(
    val quality: Int,
    val format: String,
    val new_description: String,
    val display_desc: String
)
