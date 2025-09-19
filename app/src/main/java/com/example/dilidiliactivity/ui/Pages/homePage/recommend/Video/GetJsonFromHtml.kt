package com.example.dilidiliactivity.ui.Pages.homePage.recommend.Video

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import com.example.dilidiliactivity.data.local.RandomVideoData.VideoInfo
import androidx.media3.common.util.UnstableApi

class GetBaseUrl {
}

@UnstableApi
@Composable
fun PlayInfoScreen(url: String) {
    val TAG = "PlayInfoScreen"

    //从html文件中找出<script>标签中包含window.__playinfo__的JSON数据
    val playInfo by produceState<String?>(initialValue = null, url) {
        value = fetchPlayInfoJson(url)
    }
    val coroutineScope = rememberCoroutineScope()
    var videoUrl by remember { mutableStateOf<String?>(null) }
    var audioUrl by remember { mutableStateOf<String?>(null) }
    // 进入页面时只执行一次
    LaunchedEffect(url) {
        val (v, a) = fetchVideoAndAudioUrls(url)
        videoUrl = v
        audioUrl = a
    }
    Log.d(TAG, "videoUrl:\n $videoUrl")
    Log.d(TAG, "audioUrl:\n $audioUrl")



//    Media3Player(
//        context = LocalContext.current,
//        videoUrl = videoUrl.toString(),
//        audioUrl = audioUrl.toString(),
//        modifier = Modifier.fillMaxSize()
//    )

//    DashPlayer(videoUrl.toString(),audioUrl.toString())


    //从json信息中获取视频和音频的url
//    val jsonClass = VideoUrlExtractor().extractUrls(playInfo)
//    val videoUrl = jsonClass.videoUrls?.first()
//    val audioUrl = jsonClass.audioUrls?.first()
    // playInfo 可能是整页 HTML，也可能是纯 JSON 字符串
//    val urls = remember(playInfo) { extractVideoAudioUrlsFromHtmlOrJson(playInfo) }
//    // urls?.first 是视频，urls?.second 是音频
//    val videoUrl = urls?.first
//    val audioUrl = urls?.second
//    Log.d(TAG, "videoUrl: $videoUrl \n audioUrl: $audioUrl")
    Log.d(TAG, "playInfo: \n$playInfo")
    if (playInfo == null) {
        Text("加载中...")
    } else {
        // 这里只是显示原始 JSON，可以进一步解析
//        Text(
//            text = "videoUrl:\n"+videoUrl ?: "未找到",
//            modifier = Modifier.padding(16.dp)
//        )
//        Text(
//            text = "audioUrl:\n"+audioUrl ?: "未找到",
//            modifier = Modifier.padding(16.dp)
//        )
    }
}



//从html文件中找出<script>标签中包含window.__playinfo__的JSON数据
suspend fun fetchPlayInfoJson(url: String): String? = withContext(Dispatchers.IO) {
    try {
        val doc = Jsoup.connect(url).get()
        // 找出所有 <script> 标签
        val scripts = doc.getElementsByTag("script")
        // 过滤出包含 window.__playinfo__ 的那段
        val playInfoScript = scripts.firstOrNull { it.html().contains("window.__playinfo__") }


        playInfoScript?.html()?.let { scriptContent ->
            // 去掉前缀 window.__playinfo__=
            val jsonPart = scriptContent.substringAfter("window.__playinfo__=")
            // 有些情况后面可能还有 ; 结尾，去掉
            jsonPart.substringBefore(";").trim()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

//从json信息中获取视频和音频的url
suspend fun fetchVideoAndAudioUrls(url: String): Pair<String?, String?> {
    val jsonStr = fetchPlayInfoJson(url) ?: return null to null

    val videoInfo = Json {
        ignoreUnknownKeys = true
    }.decodeFromString<VideoInfo>(jsonStr)

    val videoUrl = videoInfo.data?.dash?.video?.firstOrNull()?.baseUrl
    val audioUrl = videoInfo.data?.dash?.audio?.firstOrNull()?.baseUrl

    return videoUrl to audioUrl
}

