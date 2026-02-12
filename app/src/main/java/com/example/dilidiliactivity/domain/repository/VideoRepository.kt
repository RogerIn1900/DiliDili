package com.example.dilidiliactivity.domain.repository

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
import timber.log.Timber

class VideoRepository(
    private val dao: ArchiveDao,
    private val api: BilibiliApi
) {
    val BASE_PLAY_URL = "https://player.bilibili.com/player.html"
    private val memoryCache = mutableMapOf<String, Archive>()

    suspend fun getVideoDetail(videoId: String): Archive? {
        memoryCache[videoId]?.let { return it }
        Timber.d("内存缓存未命中: %s", videoId)

        dao.getArchive(videoId)?.let {
            memoryCache[videoId] = it.toDomain()
            return memoryCache[videoId]
        }
        Timber.d("本地数据库未命中: %s", videoId)

        return null
    }

    suspend fun getPopularPrecious(): List<Archive> {
        var _preciousState = MutableStateFlow<PopularPreciousResponse?>(null)
        val _list = MutableStateFlow<List<Archive>>(emptyList())
        val list: StateFlow<List<Archive>> = _list

        val response = api.getPopularPrecious()
        _preciousState.value = response
        if (response != null) {
            Timber.d("获取每周必看成功")
            return response.data.list
        }

        return list.value
    }

    suspend fun getVideoList(ps: Int, rid: Int): List<Archive> {
        val response = api.getDynamicRegion(ps, rid)
        val list = response.data.archives

        Timber.d("getVideoList: 获取到 %d 条视频", list.size)
        list.forEach {
            dao.insertArchive(it.toEntity())
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
            Timber.d("获取相关推荐视频成功")
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
