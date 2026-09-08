package com.example.dilidiliactivity.domain.repository

import com.example.dilidiliactivity.data.local.PopularPreciousResponse.PopularPreciousResponse
import com.example.dilidiliactivity.data.local.RelatedVideos.RelatedVideosResponse
import com.example.dilidiliactivity.data.local.VideoPlayerData.FansResponse
import com.example.dilidiliactivity.data.local.archive.ArchiveDao
import com.example.dilidiliactivity.data.local.archive.toDomain
import com.example.dilidiliactivity.data.local.archive.toEntity
import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import com.example.dilidiliactivity.data.local.archive.Archive
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException

class VideoRepository(
    private val dao: ArchiveDao,
    private val api: BilibiliApi
) {
    val BASE_PLAY_URL = "https://player.bilibili.com/player.html"
    private val refreshMutex = Mutex()

    suspend fun getVideoDetail(videoId: String): Archive? = dao.getArchive(videoId)?.toDomain()

    fun observeRegion(regionId: Int): Flow<List<Archive>> =
        dao.observeRegion(regionId).map { rows -> rows.map { it.toDomain() } }

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

    suspend fun getVideoList(ps: Int, rid: Int): List<Archive> = refreshMutex.withLock {
        require(ps > 0 && rid > 0) { "Page size and region must be positive" }
        val response = api.getDynamicRegion(ps, rid)
        if (response.code != 0) throw IOException("视频列表请求失败 (${response.code})")
        // Only a complete successful response replaces the visible snapshot.
        dao.replaceRegion(rid, response.data.archives.map { it.toEntity() })
        dao.getRegion(rid).map { it.toDomain() }
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
            if (body.code != 0) return null
            body.data.durl.firstOrNull()?.url?.takeIf { it.isNotBlank() }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            null
        }
    }
}
