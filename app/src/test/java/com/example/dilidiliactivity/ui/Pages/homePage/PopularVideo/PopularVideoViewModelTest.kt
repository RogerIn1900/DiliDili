package com.example.dilidiliactivity.ui.Pages.homePage.PopularVideo

import com.example.dilidiliactivity.data.local.PopularVideoData.Data
import com.example.dilidiliactivity.data.local.PopularVideoData.Dimension
import com.example.dilidiliactivity.data.local.PopularVideoData.Owner
import com.example.dilidiliactivity.data.local.PopularVideoData.PopularVideoData
import com.example.dilidiliactivity.data.local.PopularVideoData.Rights
import com.example.dilidiliactivity.data.local.PopularVideoData.Stat
import com.example.dilidiliactivity.data.local.PopularVideoData.VideoDetail
import com.example.dilidiliactivity.data.remote.api.PopularVideoApi
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PopularVideoViewModelTest {

    private lateinit var api: PopularVideoApi
    private lateinit var vm: PopularVideoViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        api = mockk()
        vm = PopularVideoViewModel(api)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPopularVideo success populates uiState and lists`() = runTest {
        val videoDetail = createVideoDetail(aid = 100L, cid = 200L)
        val response = PopularVideoData(
            code = 0,
            message = "0",
            ttl = 1,
            data = Data(
                title = "每周必看",
                media_id = "1",
                explain = "test",
                list = listOf(videoDetail)
            )
        )
        coEvery { api.getPopularVideo() } returns response

        vm.loadPopularVideo()

        assertNotNull(vm.uiState)
        assertEquals(1, vm.urlList.size)
        assertEquals(1, vm.videoInfoList.size)
        assertEquals("Test Video", vm.videoInfoList[0].title)
    }

    @Test
    fun `loadPopularVideo failure keeps uiState null`() = runTest {
        coEvery { api.getPopularVideo() } throws RuntimeException("网络错误")

        vm.loadPopularVideo()

        assertNull(vm.uiState)
        assertEquals(0, vm.urlList.size)
    }

    companion object {
        fun createVideoDetail(aid: Long = 1L, cid: Long = 1L) = VideoDetail(
            aid = aid,
            videos = 1,
            tid = 1,
            tname = "test",
            copyright = 1,
            pic = "http://example.com/pic.jpg",
            title = "Test Video",
            pubdate = 0L,
            ctime = 0L,
            desc = "",
            state = 0,
            duration = 100,
            mission_id = null,
            rights = Rights(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            owner = Owner(1L, "test_user", ""),
            stat = Stat(aid, 1000, 100, 50, 20, 10, 5, 0, 0, 200, 0, 0, 0, 0, 0),
            dynamic = "",
            cid = cid,
            dimension = Dimension(1920, 1080, 0),
            short_link_v2 = "",
            first_frame = "",
            pub_location = "",
            cover43 = null,
            tidv2 = null,
            tnamev2 = null,
            pid_v2 = null,
            pid_name_v2 = null,
            bvid = "BV001",
            season_type = 0,
            is_ogv = false,
            ogv_info = null,
            rcmd_reason = "",
            enable_vt = 0,
            ai_rcmd = null,
            achievement = ""
        )
    }
}
