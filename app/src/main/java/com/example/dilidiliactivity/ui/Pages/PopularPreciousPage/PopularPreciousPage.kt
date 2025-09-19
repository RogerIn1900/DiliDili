package com.example.dilidiliactivity.ui.Pages.PopularPreciousPage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.dilidiliactivity.data.local.archive.AppDatabase
import com.example.dilidiliactivity.domain.repository.VideoRepository
import com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage.VideoListScreen
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VideoViewModel


@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun PopularPreciousPage(
    rootNavController: NavController,
    viewModel: PopularPreciousViewModel = viewModel(
        factory = PopularPreciousViewModelFactory(
            AppDatabase.getInstance(LocalContext.current).archiveDao()
        )
    )
) {
    val state by viewModel.preciousState.collectAsState()
    val list by viewModel.list.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPopularPrecious()
    }

    when {
        state == null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("加载中...")
            }
        }
        else -> {
//            val list = state!!.data.list
//            LazyColumn {
//                items(count = list.size) { index ->
//                    val archive = list[index]
//                    Text(archive.title, modifier = Modifier.padding(12.dp))
//                }
//            }
//            for (archive in list) {
//
//            }

            VideoListScreen(list, onVideoClick = { archive->
                rootNavController.navigate("player/${archive.bvid}")
            })
        }
    }
}