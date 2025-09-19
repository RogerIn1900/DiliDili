package com.example.dilidiliactivity.ui.Pages.homePage.recommend

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.dilidiliactivity.data.local.RandomVideoData.DynamicRegionResponse
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.LaunchedEffect
import androidx.media3.common.util.UnstableApi
import com.example.dilidiliactivity.data.remote.ApiClient.ApiClient
import com.example.dilidiliactivity.ui.Pages.homePage.RandomVideo.RandomVideoViewModel
import com.example.dilidiliactivity.ui.Pages.homePage.RandomVideo.WebView


val TAG: String? = "RecommendPage"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendPage() {
//    get()
}

@UnstableApi
@Composable
fun BiliRegionScreen(
    ps: Int,
    rid: Int,
    randomVideo: RandomVideoViewModel = RandomVideoViewModel()
) {
    val TAG = "BiliRegionScreen"
    val uiState = randomVideo.uiState
    val aid = randomVideo.aid
    val cid = randomVideo.cid
    val page = 1
    val BASE_PLAY_URL = randomVideo.BASE_PLAY_URL
    var PLAY_URL:String=randomVideo.PLAY_URL
    // 使用 produceState 来启动网络请求 通过随机视频接口获取随机视频信息，response是返回结果
    val response by produceState<DynamicRegionResponse?>(initialValue = null, ps, rid) {
        value = try {
            ApiClient.api.getDynamicRegion(ps, rid)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    LaunchedEffect(Unit) {
        randomVideo.loadRandomVideos(ps = 1, rid = 1)
    }


//    if (randomVideo.PLAY_URL!=null){
//        PLAY_URL = randomVideo.PLAY_URL
//
//    }

    Log.d(TAG, "PLAY_URL: \n$PLAY_URL")
//    WebView(url = PLAY_URL)




//
//    //根据随机视频接口返回的简单信息 UI 部分
//    if (response == null) {
//        Text("加载中...")
//    } else {
//        Column {
//            Text("返回 code = ${response!!.code}, message = ${response!!.message}")
//            response!!.data?.archives?.forEach { archive ->
//                Text("标题:   ${archive.title}")
//                Text("BV号:   ${archive.bvid}")
//                Text("UP主:   ${archive.owner.name}")
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//        }
//    }

    //根据随机视频接口返回的完整信息 UI 部分
    if (response == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("加载中...")
        }
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                //随机视频接口返回的内容，使用archives容器装载

                //从返回结果中提取视频
                val archives = response!!.data.archives
                Log.d(TAG, "archives" + archives)
                if (archives.isEmpty()) {
                    Text("没有视频数据")
                } else {
                    archives.forEach { archive ->
                        val title = archive.title
                        val upName = archive.owner.name
                        val bvid = archive.bvid
                        val coverUrl = archive.pic
                        val shortLink = archive.short_link_v2

                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
//                            Text(
//                                "标题: ${archive.title}",
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                            Text(
//                                "BV号: ${archive.bvid}",
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                            Text(
//                                "UP主: ${archive.owner.name}",
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                            Text(
//                                "short_link_v2: ${archive.short_link_v2}",
//                                style = MaterialTheme.typography.bodyMedium
//                            )
//                            Log.d(TAG, "short_link_v2\n" + archive.short_link_v2)
//                            Spacer(modifier = Modifier.height(4.dp))


//                            PlayInfoScreen(shortLink)
                            val PLAY_URL2 = "https://player.bilibili.com/player.html?aid=115055647526824&cid=31806720339&page=1"
                            val PLAY_URL3 = "https://player.bilibili.com/player.html?aid=115032595629293&cid=31716869938&page=1"

//                            WebView(PLAY_URL)
                            if(PLAY_URL != null){
                                WebView(url = PLAY_URL)
                            }

//                            BiliVideoScreen(shortLink)

//                            BiliVideoCard(title, upName, bvid, coverUrl, shortLink)
                            Spacer(modifier = Modifier.height(4.dp))

                            // 如果有封面图片
                            archive.pic?.let { picUrl ->
                                Image(
                                    painter = rememberAsyncImagePainter(picUrl),
                                    contentDescription = archive.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Log.d(TAG, "picUrl\n" + picUrl)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                    }
                }
            }
        }
    }


}


//fun get() = runBlocking {
//    lifecycleScope.launch {
//        try {
//            val ps = 1
//            val rid = 1
//            val response = ApiClient.api.getDynamicRegion(ps, rid)
//            println("返回 code = ${response.code}, message = ${response.message}")
//            Log.d(TAG,"返回 code = ${response.code}, message = ${response.message}")
//
//            response.data?.archives?.forEach { archive ->
//                println("视频标题: ${archive.title}, BV号: ${archive.bvid}, UP主: ${archive.owner.name}")
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//}

