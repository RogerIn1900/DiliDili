package com.example.dilidiliactivity.ui.Pages.homePage.RelatedVideo

import android.view.Display
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dilidiliactivity.data.local.DetailsPageData.VideoInfo
import com.example.dilidiliactivity.data.local.RelatedVideos.RelatedVideosResponse
import com.example.dilidiliactivity.data.local.archive.AppDatabase
import com.example.dilidiliactivity.data.mapper.toUiModel
import com.example.dilidiliactivity.domain.repository.VideoRepository
import com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage.VideoListScreen
import com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage.VideoUiState
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VideoIntroCard
import kotlin.contracts.Effect

@Composable
fun RelatedVideo(
    bvid :String,
    rootNavController: NavController,
    viewModel: RelatedVideoViewModel = viewModel (
        factory = RelatedVideoViewModelFactory(
            repo = VideoRepository(AppDatabase.getInstance(LocalContext.current).archiveDao())
        )
    )
){
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(bvid) {
        viewModel.loadVideos(bvid)
    }
    when(uiState){
        is VideoUiState.Loading ->{
            Text("加载中...")
        }
        is VideoUiState.Success ->{
            val archives = (uiState as VideoUiState.Success).archives
//            VideoListScreen(
//                archives = archives,
//                onVideoClick = { archive ->
//                    rootNavController.navigate("player/${archive.bvid}")
//                }
//            )


//            Column (
//                modifier = Modifier.verticalScroll(rememberScrollState())
//                    .fillMaxSize()
//            ){
//                archives.forEach {archive ->
//                    val uiModel = archive.toUiModel()  // 调用前面写的 mapper
//                    VideoIntroCard(
//                        uiModel,
//                        onVideoClick = {
//                            rootNavController.navigate("player/${archive.bvid}")
//                        }
//                    )
//
//                }
//            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(archives) { archive ->
                    val uiModel = archive.toUiModel()
                    VideoIntroCard(
                        uiModel,
                        onVideoClick = {
                            rootNavController.navigate("player/${archive.bvid}")
                        }
                    )
                }
            }

        }
        is VideoUiState.Error -> {
            Text((uiState as VideoUiState.Error).message)
        }
    }
}