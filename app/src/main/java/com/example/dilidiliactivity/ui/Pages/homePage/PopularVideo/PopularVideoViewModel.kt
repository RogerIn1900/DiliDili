package com.example.dilidiliactivity.ui.Pages.homePage.PopularVideo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dilidiliactivity.data.remote.api.PopularVideoApi
import com.example.dilidiliactivity.data.local.PopularVideoData.PopularVideoData
import com.example.dilidiliactivity.data.local.PopularVideoData.ShowInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PopularVideoViewModel @Inject constructor(
    private val api: PopularVideoApi
) : ViewModel(){
    var uiState by mutableStateOf<PopularVideoData?>(null)
    var idList by mutableStateOf<List<Pair<String, String>>>(emptyList())
    var urlList by mutableStateOf<List<String>>(emptyList())
    var videoInfoList by mutableStateOf<List<ShowInfo>>(emptyList())

    fun loadPopularVideo(){
        viewModelScope.launch {
            try {
                uiState = api.getPopularVideo()

                idList = uiState!!.data.list.map { Pair(it.aid.toString(), it.cid.toString()) }
                urlList = idList.map { "https://player.bilibili.com/player.html?aid=${it.first}&cid=${it.second}&page=1" }

                videoInfoList = uiState!!.data.list.map { video ->
                    ShowInfo(
                        cover = video.pic,
                        duration = video.duration,
                        title = video.title,
                        like = video.stat.like,
                        view = video.stat.view,
                        danmaku = video.stat.danmaku,
                        publisher = video.owner.name,
                        pubdate = video.pubdate
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "加载热门视频失败")
            }
        }
    }

}
