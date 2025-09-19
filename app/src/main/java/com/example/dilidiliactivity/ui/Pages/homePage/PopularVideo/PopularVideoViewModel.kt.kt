package com.example.dilidiliactivity.ui.Pages.homePage.PopularVideo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dilidiliactivity.data.remote.ApiClient.PopularVideoApiClient
import com.example.dilidiliactivity.data.local.PopularVideoData.PopularVidelData
import com.example.dilidiliactivity.data.local.PopularVideoData.ShowInfo
import kotlinx.coroutines.launch

class PopularVideoViewModel : ViewModel(){
    var uiState by mutableStateOf<PopularVidelData?>(null)
//    var aid by mutableStateOf<String>("")
//    var cid by mutableStateOf<String>("")
//    var PLAY_PATH by mutableStateOf<String>("")
    var idList by mutableStateOf<List<Pair<String, String>>>(emptyList())
    //视频播放地址列表
    var urlList by mutableStateOf<List<String>>(emptyList())
    //视频展示简介列表
    var videoInfoList by mutableStateOf<List<ShowInfo>>(emptyList())

    fun loadPopularVideo(){
        viewModelScope.launch {
            try {
                uiState = PopularVideoApiClient.api.getPopularVideo()

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

//                aid = uiState!!.data.list[0].aid.toString()
//                cid = uiState!!.data.list[0].cid.toString()
//                PLAY_PATH = "https://player.bilibili.com/player.html?aid=$aid&cid=$cid&page=1"
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

}