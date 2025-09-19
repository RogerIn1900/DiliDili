package com.example.dilidiliactivity.ui.Pages.homePage.RandomVideo

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dilidiliactivity.data.local.RandomVideoData.DynamicRegionResponse
import com.example.dilidiliactivity.data.remote.ApiClient.ApiClient
import com.example.dilidiliactivity.data.local.archive.Archive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RandomVideoViewModel : ViewModel() {
    val BASE_PLAY_URL = "https://player.bilibili.com/player.html"
    val TAG = "RandomVideoViewModel"

    var uiState by mutableStateOf<DynamicRegionResponse?>(null)
        private set

    // 存储所有视频的 URL 列表
    var playUrlList by mutableStateOf<List<String>>(emptyList())
        private set

    var aid by mutableStateOf<String>("")
        private set
    var cid by mutableStateOf<String>("")
        private set
    var page by mutableStateOf<Int>(1)
        private set
    var PLAY_URL by mutableStateOf<String>("")

    private val _videoList = MutableStateFlow<List<Archive>>(emptyList())
    val videoList: StateFlow<List<Archive>> get() = _videoList

//    fun loadRandomVideos2(ps: Int, rid: Int) {
//        viewModelScope.launch {
//            val list = repo.getVideoList(ps, rid)
//            _videoList.value = list
//        }
//    }

    fun loadRandomVideos(ps: Int, rid: Int) {

        viewModelScope.launch {
            try {
                val response = ApiClient.api.getDynamicRegion(ps, rid)
                //换成每周必看视频接口
//                val response = ApiClient.api.getPopularPrecious()
                var bvid = response.data.archives[0].bvid
                var cid = response.data.archives[0].cid
                uiState = response
                // 遍历所有 archives，生成 URL 列表
                playUrlList = response.data.archives.map { video ->
                    val bvid = video.bvid
                    val aid = video.aid
                    val cid = video.cid
                    val page = 1
                    "$BASE_PLAY_URL?aid=$aid&cid=$cid&page=$page&bvid=$bvid"

                    "$BASE_PLAY_URL?aid=$aid&cid=$cid&page=$page"
                }
                Log.d(TAG, "playUrlList: $playUrlList")
                Log.d(TAG, "bvid: $bvid \ncid: $cid")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

  }