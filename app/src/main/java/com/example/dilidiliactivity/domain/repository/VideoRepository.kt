package com.example.dilidiliactivity.domain.repository

import android.util.Log
import com.example.dilidiliactivity.data.local.PopularPreciousResponse.PopularPreciousResponse
import com.example.dilidiliactivity.data.local.RelatedVideos.RelatedVideosResponse
import com.example.dilidiliactivity.data.local.VideoPlayerData.FansResponse
import com.example.dilidiliactivity.data.local.archive.ArchiveDao
import com.example.dilidiliactivity.data.local.archive.toDomain
import com.example.dilidiliactivity.data.local.archive.toEntity
import com.example.dilidiliactivity.data.remote.ApiClient.ApiClient
import com.example.dilidiliactivity.data.remote.ApiClient.RetrofitClient
import com.example.dilidiliactivity.data.local.archive.Archive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.jvm.java

class VideoRepository(private val dao: ArchiveDao) {
    val BASE_PLAY_URL = "https://player.bilibili.com/player.html"
    val TAG = "VideoRepository"
    private val memoryCache = mutableMapOf<String, Archive>()
    private val api = RetrofitClient.instance

    // 获取视频详情（内存 → 数据库 → 网络）
    suspend fun getVideoDetail(videoId: String): Archive? {
        //检查内存缓存
        memoryCache[videoId]?.let { return it }
        Log.d(TAG, "内存缓存getVideoDetail")

        //检查本地数据库
        dao.getArchive(videoId)?.let {
            memoryCache[videoId] = it.toDomain()
            return memoryCache[videoId]//数据库存在就放入缓存
        }
        Log.d(TAG, "本地数据库getArchive")

        //请求网络：使用错误的接口，应该使用特定查找bvid对应信息的接口，但是b站没有直接通过bvid获取视频的接口
//        val response = ApiClient.api.getDynamicRegion(1, 7)
//        val result = response.data.archives.find { it.bvid == videoId }
//            ?: response.data.archives.firstOrNull()//找不到，返回第一个视频
//
//        result?.let {
//            memoryCache[videoId] = it
//            dao.insertArchive(it.toEntity())
//        }
//        return result
        return null
    }

    // 每周必看:获取视频列表（直接网络，但会存本地）获取的是每周必看视频的内容
    suspend fun getPopularPrecious(): List<Archive> {
        //获取PopularPrecious视频
        var _preciousState = MutableStateFlow<PopularPreciousResponse?>(null)
        // 把 list 改成 StateFlow
        val _list = MutableStateFlow<List<Archive>>(emptyList())
        val list: StateFlow<List<Archive>> = _list

        val response = api.getPopularPrecious()
        _preciousState.value = response
        if (response != null) {
            Log.d("PopularPreciousVM", "获取每周必看成功")
            return response.data.list
        }

        return list.value
    }

    // 随机视频:获取视频列表（直接网络，但会存本地）获取的是随机视频的内容
    suspend fun getVideoList(ps: Int, rid: Int): List<Archive> {
        //调用接口请求信息
        val response = ApiClient.api.getDynamicRegion(ps, rid)
        val list = response.data.archives

        //测试list
        Log.d(TAG, "list 类型：${list::class.java}")   // 打印 list 的类型
        Log.d(TAG, "list.size: ${list.size}")        // 打印 list 的 size
        list.forEachIndexed { index, item ->
            Log.d(TAG, "index: $index, item: $item")
        }


        Log.d(TAG, "getVideoList：list: $list")
        //存一下本地
        list.forEach {
            dao.insertArchive(it.toEntity())
            Log.d(TAG, "getVideoList读取到的archive ：" + it.toEntity().toString() + "\n")
        }
        return list
//
//        val response = ApiClient.api.getDynamicRegion(ps, rid)
//        // 遍历所有 archives，生成 URL 列表
//        val playUrlList = response.data.archives.map { video ->
//            val aid = video.aid
//            val cid = video.cid
//            val page = 1
//            "$BASE_PLAY_URL?aid=$aid&cid=$cid&page=$page"
//        }
//
//        Log.d(TAG, "playUrlList: $playUrlList")
    }

    //数据库通过bvid查询archive数据
    suspend fun getArchiveByBvid(bvid: String): Archive? {
        return dao.getArchiveByBvid(bvid)?.toDomain()
    }

    //获取粉丝数
    suspend fun getFollowers(mid: Long): Int {
        var fansResponse: FansResponse = ApiClient.api.getFollowers(mid)
        return fansResponse.data.follower
    }

    //相关推荐视频
    suspend fun getRelatedVideo(bvid:String,aid:String = ""): List<Archive>{
        var relatedVideos: RelatedVideosResponse = ApiClient.api.getRelatedVideo(aid,bvid)
        if (relatedVideos != null){
            Log.d("RelatedVideoVM", "获取相关推荐视频成功")
            Log.d(TAG,"将数据存入Room数据库")
            relatedVideos.data.forEach {
                dao.insertArchive(it.toEntity())
            }
        }
        return relatedVideos.data
    }
}
