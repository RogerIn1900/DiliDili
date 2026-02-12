package com.example.dilidiliactivity.domain.repository

import android.util.Log
import com.example.dilidiliactivity.data.local.PopularPreciousResponse.PopularPreciousResponse
import com.example.dilidiliactivity.data.local.RelatedVideos.RelatedVideosResponse
import com.example.dilidiliactivity.data.local.VideoPlayerData.FansResponse
import com.example.dilidiliactivity.data.local.archive.ArchiveDao
import com.example.dilidiliactivity.data.local.archive.toDomain
import com.example.dilidiliactivity.data.local.archive.toEntity
import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import com.example.dilidiliactivity.data.local.archive.Archive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VideoRepository(
    private val dao: ArchiveDao,
    private val api: BilibiliApi
) {
    val BASE_PLAY_URL = "https://player.bilibili.com/player.html"
    val TAG = "VideoRepository"
    private val memoryCache = mutableMapOf<String, Archive>()

    suspend fun getVideoDetail(videoId: String): Archive? {
        memoryCache[videoId]?.let { return it }
        Log.d(TAG, "内存缓存getVideoDetail")

        dao.getArchive(videoId)?.let {
            memoryCache[videoId] = it.toDomain()
            return memoryCache[videoId]
        }
        Log.d(TAG, "本地数据库getArchive")

        return null
    }

    suspend fun getPopularPrecious(): List<Archive> {
        var _preciousState = MutableStateFlow<PopularPreciousResponse?>(null)
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

    suspend fun getVideoList(ps: Int, rid: Int): List<Archive> {
        val response = api.getDynamicRegion(ps, rid)
        val list = response.data.archives

        Log.d(TAG, "list 类型：${list::class.java}")
        Log.d(TAG, "list.size: ${list.size}")
        list.forEachIndexed { index, item ->
            Log.d(TAG, "index: $index, item: $item")
        }

        Log.d(TAG, "getVideoList：list: $list")
        list.forEach {
            dao.insertArchive(it.toEntity())
            Log.d(TAG, "getVideoList读取到的archive ：" + it.toEntity().toString() + "\n")
        }
        return list
    }

    suspend fun getArchiveByBvid(bvid: String): Archive? {
        return dao.getArchiveByBvid(bvid)?.toDomain()
    }

    suspend fun getFollowers(mid: Long): Int {
        var fansResponse: FansResponse = api.getFollowers(mid)
        return fansResponse.data.follower
    }

    suspend fun getRelatedVideo(bvid: String, aid: String = ""): List<Archive> {
        var relatedVideos: RelatedVideosResponse = api.getRelatedVideo(aid, bvid)
        if (relatedVideos != null) {
            Log.d("RelatedVideoVM", "获取相关推荐视频成功")
            Log.d(TAG, "将数据存入Room数据库")
            relatedVideos.data.forEach {
                dao.insertArchive(it.toEntity())
            }
        }
        return relatedVideos.data
    }

    suspend fun getPlayUrl(cid: String, bvid: String, qn: Int = 80): String? {
        return try {
            val resp = api.getPlayUrl(cid = cid, bvid = bvid, qn = qn)
            if (!resp.isSuccessful) return null
            val body = resp.body() ?: return null
            body.data.durl.firstOrNull()?.url
        } catch (_: Exception) {
            null
        }
    }
}
