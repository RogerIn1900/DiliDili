package com.example.dilidiliactivity.ui.Pages.homePage.recommend

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.util.*
import kotlin.random.Random
import java.time.Instant

class getBURl {

}




@Composable
fun VideoPlayer(url: String) {
    // 保持播放器实例
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    // 释放资源
    DisposableEffect(AndroidView(factory = { context ->
        PlayerView(context).apply {
            player = exoPlayer
        }
    }, modifier = Modifier.fillMaxSize())) {
        onDispose {
            exoPlayer.release()
        }
    }
}

// ---------- 可变参数生成方法 ----------

// 1️⃣ 生成视频 ID（这里示例直接传入，也可以封装解析 BV 号的方法）
fun getVideoId(bvid: String): String {
    // 真实环境可以通过 B 站接口解析 BV -> videoId
    return bvid // 示例直接返回
}

// 2️⃣ 生成分片路径
fun getPartPath(videoId: String): String {
    // 真实环境可以通过播放接口返回，这里模拟
    val randomFolder1 = Random.nextInt(20, 40)
    val randomFolder2 = Random.nextInt(70, 80)
    return "$randomFolder1/$randomFolder2"
}

// 3️⃣ 生成 m4s 文件名
fun getM4sFileName(videoId: String): String {
    // 分片文件名一般格式: videoId-1-xxxxx.m4s
    val partNumber = Random.nextInt(10000, 100000)
    return "$videoId-1-$partNumber.m4s"
}

// 4️⃣ 生成 trid（唯一请求标识）
fun generateTrid(): String {
    return UUID.randomUUID().toString().replace("-", "") + "u"
}

// 5️⃣ 生成 deadline（有效期时间戳，单位秒）
@RequiresApi(Build.VERSION_CODES.O)
fun generateDeadline(secondsValid: Long = 600): Long {
    return Instant.now().epochSecond + secondsValid
}

// 6️⃣ upsig（签名），**推荐使用官方接口返回**
// 这里仅示例传入或默认值，不建议手动生成
fun getUpsig(): String {
    return "exampleUpsig1234567890abcdef"
}

// 7️⃣ bw（带宽/码率）
fun generateBw(): Int {
    // 模拟返回随机码率
    return Random.nextInt(50000, 600000)
}

// ---------- URL 拼接方法 ----------
fun buildBiliVideoUrl2(
    videoId: String,
    partPath: String,
    m4sFile: String,
    trid: String,
    deadline: Long,
    upsig: String,
    bw: Int
): String {
    val baseUrl = "https://upos-hz-mirrorakam.akamaized.net/upgcxcode"
    return "$baseUrl/$partPath/$videoId/$m4sFile" +
            "?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=" +
            "&og=hw" +
            "&platform=pc" +
            "&mid=0" +
            "&oi=3113718418" +
            "&nbs=1" +
            "&deadline=$deadline" +
            "&uipk=5" +
            "&trid=$trid" +
            "&gen=playurlv3" +
            "&os=akam" +
            "&upsig=$upsig" +
            "&uparams=e,og,platform,mid,oi,nbs,deadline,uipk,trid,gen,os" +
            "&hdnts=exp=$deadline~hmac=47e5ada55c39b3957ed23c3105ac3026e95af6a0c57240f85458ab2ae67b2d2d" +
            "&bvc=vod" +
            "&nettype=0" +
            "&bw=$bw" +
            "&agrr=0" +
            "&buvid=9A02441B-4C93-D048-AE13-0D6C1242520073956infoc" +
            "&build=0" +
            "&dl=0" +
            "&f=u_0_0" +
            "&orderid=0,2"
}

//// ---------- 示例调用 ----------
//@RequiresApi(Build.VERSION_CODES.O)
//fun main() {
//    val videoId = getVideoId("31214207828")
//    val partPath = getPartPath(videoId)
//    val m4sFile = getM4sFileName(videoId)
//    val trid = generateTrid()
//    val deadline = generateDeadline()
//    val upsig = getUpsig()
//    val bw = generateBw()
//
//    val videoUrl = buildBiliVideoUrl(videoId, partPath, m4sFile, trid, deadline, upsig, bw)
//    println("生成的视频 URL: $videoUrl")
//}
