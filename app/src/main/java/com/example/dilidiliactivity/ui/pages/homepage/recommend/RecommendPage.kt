package com.example.dilidiliactivity.ui.pages.homepage.recommend

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.lazy.LazyColumn
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.example.dilidiliactivity.anr.AnrMonitor
import com.example.dilidiliactivity.anr.AnrStage
import com.example.dilidiliactivity.ui.pages.homepage.randomvideo.RandomVideoViewModel
import timber.log.Timber
import com.example.dilidiliactivity.ui.pages.homepage.randomvideo.WebView


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
    randomVideo: RandomVideoViewModel = hiltViewModel()
) {
    val TAG = "BiliRegionScreen"
    val uiState = randomVideo.uiState
    val BASE_PLAY_URL = randomVideo.BASE_PLAY_URL
    var PLAY_URL: String = randomVideo.PLAY_URL

    LaunchedEffect(Unit) {
        randomVideo.loadRandomVideos(ps = ps, rid = rid)
    }

    Timber.d("PLAY_URL: %s", PLAY_URL)

    if (uiState == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("加载中...")
        }
    } else {
        AnrMonitor.setStage(AnrStage.IMAGE_LOAD)
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                val archives = uiState.data.archives
                Timber.d("archives: %s", archives)
                if (archives.isEmpty()) {
                    Text("没有视频数据")
                } else {
                    archives.forEach { archive ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            if (PLAY_URL != null) {
                                WebView(url = PLAY_URL)
                            }

                            Spacer(modifier = Modifier.height(4.dp))

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
                                Timber.d("picUrl: %s", picUrl)
                            }
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}
