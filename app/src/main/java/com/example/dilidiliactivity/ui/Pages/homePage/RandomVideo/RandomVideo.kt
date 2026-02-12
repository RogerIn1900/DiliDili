package com.example.dilidiliactivity.ui.Pages.homePage.RandomVideo

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import timber.log.Timber

@Composable
fun RandomVideo(randomVideo: RandomVideoViewModel = hiltViewModel()) {
    val uiState = randomVideo.uiState
    val ps = 7
    val rid = 1
    var playUrlList = randomVideo.playUrlList

    LaunchedEffect(Unit) {
        randomVideo.loadRandomVideos(ps = ps, rid = rid)
    }
    Timber.d("PLAY_URL: %s", randomVideo.PLAY_URL)

    if (uiState == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("加载中...")
        }
    } else {
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
    }
}
