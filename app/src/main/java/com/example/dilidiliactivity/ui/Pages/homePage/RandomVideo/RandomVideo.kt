package com.example.dilidiliactivity.ui.Pages.homePage.RandomVideo

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dilidiliactivity.data.local.RandomVideoData.DynamicRegionResponse
import com.example.dilidiliactivity.data.remote.ApiClient.ApiClient

@Composable
fun RandomVideo(randomVideo:RandomVideoViewModel = RandomVideoViewModel()) {
    val TAG = "RandomVideo"
    val uiState = randomVideo.uiState
    val aid = randomVideo.aid
    val cid = randomVideo.cid
    val page = 1
    val BASE_PLAY_URL = randomVideo.BASE_PLAY_URL
    val ps = 7
    val rid = 1
    var PLAY_URL:String=randomVideo.PLAY_URL
    var playUrlList = randomVideo.playUrlList


    val response by produceState<DynamicRegionResponse?>(initialValue = null, ps, rid) {
        value = try {
            ApiClient.api.getDynamicRegion(ps, rid)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    LaunchedEffect(Unit) {
        randomVideo.loadRandomVideos(ps = 7, rid = 1)
    }
    Log.d(TAG, "PLAY_URL: \n$PLAY_URL")

    //根据随机视频接口返回的完整信息 UI 部分
    if (response == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("加载中...")
        }
    } else {
        // 直接使用webview跳转url链接直接播放视频，数据加载完后渲染视频,
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(playUrlList.size) { index ->
                val playUrl = playUrlList[index]
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(color = Color.Gray)
                ) {
                    WebView(url = playUrl)
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                }
            }
        }

        // 使用视频封面和视频简介 UI 部分，点击之后跳转到播放页面

    }



}

