package com.example.dilidiliactivity.ui.pages.homepage.animatepage

import com.example.dilidiliactivity.data.local.archive.*
import com.example.dilidiliactivity.domain.repository.VideoRepository
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnimateVideoViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: VideoRepository
    private lateinit var vm: AnimateVideoViewModel
    private val rows = MutableStateFlow<List<Archive>>(emptyList())
    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        repo = mockk()
        every { repo.observeRegion(1) } returns rows
        vm = AnimateVideoViewModel(repo)
    }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun `network failure retains database rows for offline reading`() = runTest(dispatcher) {
        rows.value = listOf(createArchive("cached"))
        coEvery { repo.getVideoList(10, 1) } throws java.io.IOException("offline")
        vm.loadVideos(); runCurrent()
        assertEquals("cached", vm.allArchives.value.single().bvid)
        assertTrue(vm.uiState.value is VideoUiState.Error)
        assertFalse(vm.isRefreshing.value)
    }
    @Test fun `database emission replaces UI independently of request result`() = runTest(dispatcher) {
        coEvery { repo.getVideoList(10, 1) } returns listOf(createArchive("unpublished"))
        vm.loadVideos(); runCurrent()
        assertTrue(vm.allArchives.value.isEmpty())
        rows.value = listOf(createArchive("persisted")); runCurrent()
        assertEquals("persisted", vm.allArchives.value.single().bvid)
    }
    @Test fun `overlapping refresh gestures share one in flight request`() = runTest(dispatcher) {
        val pending = CompletableDeferred<List<Archive>>()
        coEvery { repo.getVideoList(10, 1) } coAnswers { pending.await() }
        vm.loadVideos(); runCurrent(); vm.refreshVideos(); runCurrent()
        coVerify(exactly = 1) { repo.getVideoList(10, 1) }
        pending.complete(emptyList()); runCurrent()
        assertFalse(vm.isRefreshing.value)
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
