package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dilidiliactivity.data.local.PopularPreciousResponse.PopularPreciousResponse
import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val api: BilibiliApi
) : ViewModel() {

    private val _videoUrl = MutableStateFlow<String?>(null)
    val videoUrl: StateFlow<String?> = _videoUrl

    private var _preciousState = MutableStateFlow<PopularPreciousResponse?>(null)
    val preciousState: StateFlow<PopularPreciousResponse?> = _preciousState

    fun fetchPopularPrecious() {
        viewModelScope.launch {
            try {
                val response = api.getPopularPrecious()
                _preciousState.value = response
            } catch (e: Exception) {
                Timber.e(e, "获取每周必看失败")
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
