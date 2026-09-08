package com.example.dilidiliactivity.domain.repository

import com.example.dilidiliactivity.data.local.RandomVideoData.Data
import com.example.dilidiliactivity.data.local.RandomVideoData.DynamicRegionResponse
import com.example.dilidiliactivity.data.local.RandomVideoData.Page
import com.example.dilidiliactivity.data.local.RelatedVideos.RelatedVideosResponse
import com.example.dilidiliactivity.data.local.VideoPlayerData.FansData
import com.example.dilidiliactivity.data.local.VideoPlayerData.FansResponse
import com.example.dilidiliactivity.data.local.archive.ArchiveDao
import com.example.dilidiliactivity.data.local.archive.ArchiveEntity
import com.example.dilidiliactivity.data.local.archive.toEntity
import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import com.example.dilidiliactivity.ui.pages.homepage.animatepage.AnimateVideoViewModelTest.Companion.createArchive
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class VideoRepositoryTest {

    private lateinit var dao: ArchiveDao
    private lateinit var api: BilibiliApi
    private lateinit var repo: VideoRepository

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        api = mockk()
        repo = VideoRepository(dao, api)
    }

    @Test
    fun `getVideoDetail returns from memory cache on second call`() = runTest {
        val archive = createArchive("BV001")
        val entity = archive.toEntity()
        coEvery { dao.getArchive("BV001") } returns entity

        // 第一次调用 — 内存缓存未命中，从 DAO 获取
        val first = repo.getVideoDetail("BV001")
        assertNotNull(first)

        // 第二次调用 — 应该从内存缓存返回，不再查询 DAO
        val second = repo.getVideoDetail("BV001")
        assertNotNull(second)
        assertEquals(first, second)

        // DAO 只被调用一次（第一次）
        coVerify(exactly = 1) { dao.getArchive("BV001") }
    }

    @Test
    fun `getVideoDetail returns null when not in cache or db`() = runTest {
        coEvery { dao.getArchive("BV_MISSING") } returns null

        val result = repo.getVideoDetail("BV_MISSING")
        assertNull(result)
    }

    @Test
    fun `getVideoList fetches from API and saves to DAO`() = runTest {
        val archives = listOf(createArchive("BV001"), createArchive("BV002"))
        val response = DynamicRegionResponse(
            code = 0,
            message = "0",
            ttl = 1,
            data = Data(
                page = Page(num = 1, size = 10, count = 2),
                archives = archives
            )
        )
        coEvery { api.getDynamicRegion(10, 1) } returns response

        val result = repo.getVideoList(10, 1)

        assertEquals(2, result.size)
        assertEquals("BV001", result[0].bvid)
        coVerify(exactly = 2) { dao.insertArchive(any()) }
    }

    @Test
    fun `getFollowers returns follower count`() = runTest {
        coEvery { api.getFollowers(12345L) } returns FansResponse(
            code = 0,
            message = "0",
            ttl = 1,
            data = FansData(mid = 12345L, following = 100, whisper = 0, black = 0, follower = 9999)
        )

        val result = repo.getFollowers(12345L)
        assertEquals(9999, result)
    }

    @Test
    fun `getRelatedVideo fetches and caches results`() = runTest {
        val related = listOf(createArchive("BV_R1"), createArchive("BV_R2"))
        coEvery { api.getRelatedVideo("", "BV001") } returns RelatedVideosResponse(
            code = 0,
            message = "0",
            data = related
        )

        val result = repo.getRelatedVideo("BV001")
        assertEquals(2, result.size)
        coVerify(exactly = 2) { dao.insertArchive(any()) }
    }
}
