package com.example.dilidiliactivity.ui.Pages.homePage.RelatedVideo

import app.cash.turbine.test
import com.example.dilidiliactivity.domain.repository.VideoRepository
import com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage.AnimateVideoViewModelTest.Companion.createArchive
import com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage.VideoUiState
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
class RelatedVideoViewModelTest {

    private lateinit var repo: VideoRepository
    private lateinit var vm: RelatedVideoViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repo = mockk()
        vm = RelatedVideoViewModel(repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadVideos success emits Success state`() = runTest {
        val archives = listOf(createArchive("BV_R1"), createArchive("BV_R2"))
        coEvery { repo.getRelatedVideo("BV001", "") } returns archives

        vm.uiState.test {
            assertEquals(VideoUiState.Loading, awaitItem())

            vm.loadVideos("BV001")

            val state = awaitItem()
            assertTrue(state is VideoUiState.Success)
            assertEquals(2, (state as VideoUiState.Success).archives.size)
        }
    }

    @Test
    fun `loadVideos failure emits Error state`() = runTest {
        coEvery { repo.getRelatedVideo("BV_BAD", "") } throws RuntimeException("网络超时")

        vm.uiState.test {
            assertEquals(VideoUiState.Loading, awaitItem())

            vm.loadVideos("BV_BAD")

            val state = awaitItem()
            assertTrue(state is VideoUiState.Error)
            assertTrue((state as VideoUiState.Error).message.contains("网络超时"))
        }
    }
}
