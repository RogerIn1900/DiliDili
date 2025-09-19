package com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dilidiliactivity.data.local.archive.AppDatabase
import com.example.dilidiliactivity.data.mapper.toUiModel
import com.example.dilidiliactivity.data.local.archive.Archive
import com.example.dilidiliactivity.domain.repository.VideoRepository
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VideoIntroCard

@Composable
fun AnimatePage(
    rootNavController: NavController,
    viewModel: AnimateVideoViewModel = viewModel(
        factory = AnimateVideoViewModelFactory(
            VideoRepository(AppDatabase.getInstance(LocalContext.current).archiveDao())
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    // 页面启动时加载数据
    LaunchedEffect(Unit) {
        viewModel.loadVideos()
    }
    when (uiState) {
        is VideoUiState.Loading -> {
            Text("加载中...")
        }
        is VideoUiState.Success -> {
            val archives = (uiState as VideoUiState.Success).archives
            VideoListScreen(
                archives = archives,
                onVideoClick = { archive ->
                    rootNavController.navigate("player/${archive.bvid}")
                }
            )
        }
        is VideoUiState.Error -> {
            Text((uiState as VideoUiState.Error).message)
        }
    }
}
@Composable
fun VideoListScreen(
    archives: List<Archive>,
    onVideoClick: (Archive) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(archives) { archive ->
            val uiModel = archive.toUiModel()  // 调用前面写的 mapper
            VideoIntroCard(
                video = uiModel,
                onVideoClick = { onVideoClick(archive) }
            )
        }
    }
}
