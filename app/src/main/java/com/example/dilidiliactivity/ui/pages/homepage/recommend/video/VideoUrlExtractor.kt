//package com.example.dilidiliactivity.Pages.homePage.recommend.Video
//
//import com.example.dilidiliactivity.Bean.RandomVideoData.SupportFormat
//import org.json.JSONObject
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.decodeFromString
//import  com.example.dilidiliactivity.Bean.RandomVideoData.VideoInfo
//
//class VideoUrlExtractor {
//
//    // 解析JSON并提取URL
//    fun extractUrls(jsonString: String): VideoAudioUrls {
//        val videoInfo = Json.decodeFromString<VideoInfo>(jsonString)
//
//        val videoUrls = videoInfo.data?.dash?.video?.map { stream ->
//            VideoUrl(
//                id = stream.id,
//                quality = getQualityByStreamId(stream.id, videoInfo.data.support_formats),
//                primaryUrl = stream.baseUrl,
//                backupUrls = stream.backupUrl,
//                bandwidth = stream.bandwidth,
//                resolution = "${stream.width}x${stream.height}",
//                frameRate = stream.frameRate,
//                codec = stream.codecs
//            )
//        }
//
//        val audioUrls = videoInfo.data.dash.audio.map { stream ->
//            AudioUrl(
//                id = stream.id,
//                primaryUrl = stream.baseUrl,
//                backupUrls = stream.backupUrl,
//                bandwidth = stream.bandwidth,
//                codec = stream.codecs
//            )
//        }
//
//        return VideoAudioUrls(videoUrls, audioUrls)
//    }
//
//    // 根据流ID获取质量信息
//    private fun getQualityByStreamId(streamId: Int, formats: List<SupportFormat>): String {
//        return when (streamId) {
//            116 -> "1080P 60帧"
//            80 -> "1080P 高清"
//            64 -> "720P 准高清"
//            32 -> "480P 标清"
//            16 -> "360P 流畅"
//            else -> "未知质量"
//        }
//    }
//}
//
//// 返回数据类
//data class VideoUrl(
//    val id: Int,
//    val quality: String,
//    val primaryUrl: String,
//    val backupUrls: List<String>,
//    val bandwidth: Int,
//    val resolution: String,
//    val frameRate: String,
//    val codec: String
//)
//
//data class AudioUrl(
//    val id: Int,
//    val primaryUrl: String,
//    val backupUrls: List<String>,
//    val bandwidth: Int,
//    val codec: String
//)
//
//data class VideoAudioUrls(
//    val videoUrls: List<VideoUrl>,
//    val audioUrls: List<AudioUrl>
//)