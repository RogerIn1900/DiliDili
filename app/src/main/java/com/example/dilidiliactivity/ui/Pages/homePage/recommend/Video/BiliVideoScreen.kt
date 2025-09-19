package com.example.dilidiliactivity.ui.Pages.homePage.recommend.Video
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.json.JSONObject


data class VideoInfo(
    val title: String,
    val bvid: String,
    val pic: String,
    val author: String
)

@Composable
fun BiliVideoScreen(bvUrl: String) {
    val videoInfo by produceState<VideoInfo?>(initialValue = null, bvUrl) {
        value = withContext(Dispatchers.IO) {
            try {
                val doc = Jsoup.connect(bvUrl).get()
                val htmlString = doc.html()
                val initialStateRegex = "window\\.__INITIAL_STATE__=(\\{.*?\\})</script>".toRegex()
                val match = initialStateRegex.find(htmlString)
                val jsonStr = match?.groups?.get(1)?.value

                if (jsonStr != null) {
                    val jsonObj = JSONObject(jsonStr)
                    val videoData = jsonObj.getJSONObject("videoData")
                    VideoInfo(
                        title = videoData.getString("title"),
                        bvid = videoData.getString("bvid"),
                        pic = videoData.getString("pic"),
                        author = videoData.getJSONObject("owner").getString("name")
                    )
                } else null
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    if (videoInfo == null) {
        Text("加载中...")
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(videoInfo!!.pic),
                contentDescription = videoInfo!!.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("标题: ${videoInfo!!.title}", style = MaterialTheme.typography.titleMedium)
            Text("BV号: ${videoInfo!!.bvid}", style = MaterialTheme.typography.bodyMedium)
            Text("UP主: ${videoInfo!!.author}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
