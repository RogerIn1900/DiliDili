@file:kotlin.OptIn(ExperimentalMaterialApi::class)

package com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
    val allArchives by viewModel.allArchives.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    // 页面启动时加载数据（仅在首次加载时）
    LaunchedEffect(Unit) {
        if (allArchives.isEmpty()) {
            viewModel.loadVideos()
        }
    }
    
    when (uiState) {
        is VideoUiState.Loading -> {
            if (allArchives.isEmpty()) {
                // 首次加载时显示加载提示
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("加载中...")
                }
            } else {
                // 有数据时显示列表，即使正在刷新
                VideoListScreen(
                    archives = allArchives,
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refreshVideos() },
                    onVideoClick = { archive ->
                        rootNavController.navigate("player/${archive.bvid}")
                    }
                )
            }
        }
        is VideoUiState.Success -> {
            // 使用累积的列表
            VideoListScreen(
                archives = allArchives,
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshVideos() },
                onVideoClick = { archive ->
                    rootNavController.navigate("player/${archive.bvid}")
                }
            )
        }
        is VideoUiState.Error -> {
            if (allArchives.isEmpty()) {
                // 首次加载失败时显示错误
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text((uiState as VideoUiState.Error).message)
                }
            } else {
                // 有数据时显示列表，即使有错误
                VideoListScreen(
                    archives = allArchives,
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refreshVideos() },
                    onVideoClick = { archive ->
                        rootNavController.navigate("player/${archive.bvid}")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VideoListScreen(
    archives: List<Archive>,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onVideoClick: (Archive) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
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
        
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}
