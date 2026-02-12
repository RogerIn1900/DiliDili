package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.dilidiliactivity.data.local.PopularPreciousResponse.PopularPreciousResponse
import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val api: BilibiliApi
) : ViewModel() {

    private val _videoUrl = MutableStateFlow<String?>(null)
    val videoUrl: StateFlow<String?> = _videoUrl

    private var _preciousState = MutableStateFlow<PopularPreciousResponse?>(null)
    val preciousState: StateFlow<PopularPreciousResponse?> = _preciousState

    @OptIn(UnstableApi::class)
    fun fetchPopularPrecious() {
        viewModelScope.launch {
            try {
                val response = api.getPopularPrecious()
                _preciousState.value = response
            } catch (e: Exception) {
                Log.e("PopularPreciousVM", "获取每周必看失败: ${e.message}")
                _preciousState.value = null
            }
        }
    }

    fun fetchVideoUrl(cid: String, bvid: String) {
        viewModelScope.launch {
            try {
                val response = api.getPlayUrl(cid, bvid)
                if (response.isSuccessful) {
                    val playUrlResponse = response.body()
                    val url = playUrlResponse
                        ?.data
                        ?.durl
                        ?.firstOrNull()
                        ?.url
                    _videoUrl.value = url
                } else {
                    _videoUrl.value = null
                }
            } catch (e: Exception) {
                _videoUrl.value = null
            }
        }
    }
}
