package com.example.dilidiliactivity.ui.Pages.homePage.recommend.Video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


@Serializable
data class PlayInfo(val data: Data? = null)

@Serializable
data class Data(val dash: Dash? = null)

@Serializable
data class Dash(
    val video: List<Stream>? = null,
    val audio: List<Stream>? = null
)

@Serializable
data class Stream(
    @SerialName("baseUrl") val baseUrl: String? = null,
    @SerialName("base_url") val base_url: String? = null,
    @SerialName("backupUrl") val backupUrl: List<String>? = null,
    @SerialName("backup_url") val backup_url: List<String>? = null
)

private fun Stream.primaryUrl(): String? =
    baseUrl ?: base_url ?: backupUrl?.firstOrNull() ?: backup_url?.firstOrNull()

fun extractVideoAudioUrlsFromHtmlOrJson(input: String?): Pair<String, String>? {
    if (input.isNullOrBlank()) return null

    // 纯 JSON 直接用
    val jsonBody = if (input.trimStart().startsWith("{")) {
        input
    } else {
        // 修复：同时转义 { 和 }，并用 [\s\S] 跨行匹配
        val regex = Regex("""window\.__playinfo__=\s*(\{[\s\S]*?\})""")
        regex.find(input)?.groupValues?.getOrNull(1) ?: return null
    }

    val json = Json { ignoreUnknownKeys = true }
    val play = runCatching { json.decodeFromString<PlayInfo>(jsonBody) }.getOrNull() ?: return null

    val videoUrl = play.data?.dash?.video?.firstOrNull()?.primaryUrl()
    val audioUrl = play.data?.dash?.audio?.firstOrNull()?.primaryUrl()

    return if (!videoUrl.isNullOrBlank() && !audioUrl.isNullOrBlank()) videoUrl to audioUrl else null
}
