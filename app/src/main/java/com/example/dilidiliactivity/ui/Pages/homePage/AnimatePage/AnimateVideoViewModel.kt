package com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage

import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.dilidiliactivity.data.local.RandomVideoData.DynamicRegionResponse
import com.example.dilidiliactivity.data.remote.ApiClient.ApiClient
import com.example.dilidiliactivity.data.local.archive.Archive
import com.example.dilidiliactivity.domain.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout


sealed class VideoUiState {
    object Loading : VideoUiState()
    data class Success(val archives: List<Archive>) : VideoUiState()
    data class Error(val message: String) : VideoUiState()
}

class AnimateVideoViewModel(
    private val repo: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState

    fun loadVideos(ps: Int = 10, rid: Int = 1) {
        viewModelScope.launch {
//            _uiState.value = VideoUiState.Loading
            try {
                // 限定 9 秒超时
                val archives = withTimeout(9000L) {
                    repo.getVideoList(ps, rid)
                    //将随机视频接口换成每周必看视频接口
//                    repo.getPopularPrecious()
                }
                _uiState.value = VideoUiState.Success(archives)
            } catch (e: Exception) {
                _uiState.value = VideoUiState.Error("加载失败: ${e.message}")
            }
        }
    }
}

class AnimateVideoViewModel2  : ViewModel(){
    val BASE_PLAY_URL = "https://player.bilibili.com/player.html"
    val TAG = "AnimateVideoViewModel"

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

    @OptIn(UnstableApi::class)
    fun loadAnimateVideos(ps: Int, rid: Int) {

        viewModelScope.launch {
            try {
                val response = ApiClient.api.getDynamicRegion(ps, rid)
                uiState = response
                // 遍历所有 archives，生成 URL 列表
                playUrlList = response.data.archives.map { video ->
                    val aid = video.aid
                    val cid = video.cid
                    val page = 1
                    "$BASE_PLAY_URL?aid=$aid&cid=$cid&page=$page"
                }
                Log.d(TAG, "playUrlList: $playUrlList")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}