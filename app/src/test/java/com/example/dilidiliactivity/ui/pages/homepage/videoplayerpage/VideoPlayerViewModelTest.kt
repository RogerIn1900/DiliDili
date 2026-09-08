package com.example.dilidiliactivity.ui.pages.homepage.videoplayerpage

import com.example.dilidiliactivity.domain.repository.VideoRepository
import com.example.dilidiliactivity.ui.pages.homepage.animatepage.AnimateVideoViewModelTest.Companion.createArchive
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VideoPlayerViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repo: VideoRepository
    private lateinit var vm: VideoPlayerViewModel

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        repo = mockk()
        vm = VideoPlayerViewModel(repo)
    }
    @After fun tearDown() = Dispatchers.resetMain()

    @Test fun `successful selection publishes resolved media without network in test`() = runTest(dispatcher) {
        val archive = createArchive("BV001")
        coEvery { repo.getVideoDetail("BV001") } returns archive
        coEvery { repo.getPlayUrl("1", "BV001", any()) } returns "https://example.test/video.mp4"
        vm.loadVideo(" BV001 ")
        assertTrue(vm.uiState.value.isLoading)
        runCurrent()
        assertEquals(archive, vm.uiState.value.archive)
        assertEquals("https://example.test/video.mp4", vm.uiState.value.videoUrl)
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test fun `missing video clears loading and previous media`() = runTest(dispatcher) {
        coEvery { repo.getVideoDetail(any()) } returns null
        vm.loadVideo("missing")
        runCurrent()
        assertEquals("视频不存在", vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.isLoading)
        assertNull(vm.uiState.value.videoUrl)
    }

    @Test fun `database failure produces actionable error`() = runTest(dispatcher) {
        coEvery { repo.getVideoDetail(any()) } throws IllegalStateException("数据库异常")
        vm.loadVideo("BV001")
        runCurrent()
        assertTrue(vm.uiState.value.errorMessage!!.contains("数据库异常"))
        assertFalse(vm.uiState.value.isLoading)
    }

    @Test fun `missing media source does not publish a playable archive`() = runTest(dispatcher) {
        coEvery { repo.getVideoDetail(any()) } returns createArchive("BV001")
        coEvery { repo.getPlayUrl(any(), any(), any()) } returns null
        vm.loadVideo("BV001")
        runCurrent()
        assertEquals("无法获取视频源", vm.uiState.value.errorMessage)
        assertNull(vm.uiState.value.archive)
    }

    @Test fun `late uncancellable result cannot overwrite newer selection`() = runTest(dispatcher) {
        val oldResult = CompletableDeferred<String?>()
        coEvery { repo.getVideoDetail(any()) } answers { createArchive(firstArg()) }
        coEvery { repo.getPlayUrl("1", "old", any()) } coAnswers {
            withContext(NonCancellable) { oldResult.await() }
        }
        coEvery { repo.getPlayUrl("1", "new", any()) } returns "https://example.test/new.mp4"
        vm.loadVideo("old")
        runCurrent()
        vm.loadVideo("new")
        runCurrent()
        oldResult.complete("https://example.test/old.mp4")
        runCurrent()
        assertEquals("new", vm.uiState.value.archive!!.bvid)
        assertEquals("https://example.test/new.mp4", vm.uiState.value.videoUrl)
    }
}
