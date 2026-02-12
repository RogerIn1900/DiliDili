package com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage

import app.cash.turbine.test
import com.example.dilidiliactivity.data.local.archive.Archive
import com.example.dilidiliactivity.data.local.archive.Dimension
import com.example.dilidiliactivity.data.local.archive.Owner
import com.example.dilidiliactivity.data.local.archive.Rights
import com.example.dilidiliactivity.data.local.archive.Stat
import com.example.dilidiliactivity.domain.repository.VideoRepository
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnimateVideoViewModelTest {

    private lateinit var repo: VideoRepository
    private lateinit var vm: AnimateVideoViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk()
        vm = AnimateVideoViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadVideos success emits Success state`() = runTest {
        val archives = listOf(createArchive("BV001"), createArchive("BV002"))
        coEvery { repo.getVideoList(10, 1) } returns archives

        vm.uiState.test {
            assertEquals(VideoUiState.Loading, awaitItem())

            vm.loadVideos()

            val state = awaitItem()
            assertTrue(state is VideoUiState.Success)
            assertEquals(2, (state as VideoUiState.Success).archives.size)
        }
    }

    @Test
    fun `loadVideos failure emits Error state`() = runTest {
        coEvery { repo.getVideoList(10, 1) } throws RuntimeException("网络错误")

        vm.uiState.test {
            assertEquals(VideoUiState.Loading, awaitItem())

            vm.loadVideos()

            val state = awaitItem()
            assertTrue(state is VideoUiState.Error)
            assertTrue((state as VideoUiState.Error).message.contains("网络错误"))
        }
    }

    @Test
    fun `refreshVideos deduplicates by bvid`() = runTest {
        val initial = listOf(createArchive("BV001"), createArchive("BV002"))
        coEvery { repo.getVideoList(10, 1) } returns initial

        vm.loadVideos()

        // Refresh returns one duplicate (BV001) and one new (BV003)
        val refresh = listOf(createArchive("BV001"), createArchive("BV003"))
        coEvery { repo.getVideoList(10, 1) } returns refresh

        vm.refreshVideos()

        vm.allArchives.test {
            val list = awaitItem()
            // BV003 (new) + BV001 (existing) + BV002 (existing) = 3 unique
            assertEquals(3, list.size)
            assertEquals("BV003", list[0].bvid)
            assertEquals("BV001", list[1].bvid)
            assertEquals("BV002", list[2].bvid)
        }
    }

    companion object {
        fun createArchive(bvid: String) = Archive(
            aid = 1L,
            videos = 1,
            tid = 1,
            tname = "test",
            copyright = 1,
            pic = "",
            title = "Test $bvid",
            pubdate = 0L,
            ctime = 0L,
            desc = "",
            state = 0,
            duration = 100,
            mission_id = null,
            rights = Rights(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            owner = Owner(1L, "test_user", ""),
            stat = Stat(1L, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
            dynamic = null,
            cid = 1L,
            dimension = Dimension(1920, 1080, 0),
            short_link_v2 = "",
            first_frame = "",
            pub_location = null,
            cover43 = null,
            tidv2 = 0,
            tnamev2 = "",
            pid_v2 = 0,
            pid_name_v2 = "",
            bvid = bvid,
            season_type = 0,
            is_ogv = false,
            ogv_info = null,
            rcmd_reason = null,
            enable_vt = 0,
            ai_rcmd = null
        )
    }
}
