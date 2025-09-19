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
class GetBaseUrlFromJson {


}



val audioUrl = "https://upos-hz-mirrorakam.akamaized.net/upgcxcode/56/55/30650335556/30650335556-1-30216.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&mid=0&deadline=1755707127&gen=playurlv3&os=akam&platform=pc&uipk=5&oi=3113718418&nbs=1&og=hw&trid=ef9fbb4090f944b89da14c27303d5a5u&upsig=e3a1743d627ccc1af0adeed2d620ee2b&uparams=e,mid,deadline,gen,os,platform,uipk,oi,nbs,og,trid&hdnts=exp=1755707127~hmac=de8f698e68aedd2b40fba864c75e0e43fbbce64f0bb3f134735aac4b77b3c65a&bvc=vod&nettype=0&bw=37897&f=u_0_0&agrr=0&buvid=7185F2AF-22BE-C2BB-9470-7566F5E1F54976190infoc&build=0&dl=0&orderid=0,2"
