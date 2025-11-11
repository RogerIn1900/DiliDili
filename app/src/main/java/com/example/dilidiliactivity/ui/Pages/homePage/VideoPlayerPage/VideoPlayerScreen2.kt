package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import com.example.dilidiliactivity.data.local.ArchiveSingleton
import com.example.dilidiliactivity.data.mapper.toDisplayCount
import com.example.dilidiliactivity.data.mapper.toUiModel
import com.example.dilidiliactivity.data.mapper.toUserInfo
import com.example.dilidiliactivity.data.mapper.toVideoInfo
import com.example.dilidiliactivity.data.local.archive.AppDatabase
import com.example.dilidiliactivity.data.local.archive.Archive
import com.example.dilidiliactivity.data.local.DetailsPageData.VideoInfo
import com.example.dilidiliactivity.data.local.advertisement.AdvertisementData
import com.example.dilidiliactivity.domain.repository.VideoRepository
import com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage.VideoUiState
import com.example.dilidiliactivity.ui.Pages.homePage.RelatedVideo.RelatedVideoViewModel
import com.example.dilidiliactivity.ui.Pages.homePage.RelatedVideo.RelatedVideoViewModelFactory
import com.example.dilidiliactivity.R
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen2(
    rootNavController: NavController,
    videoId: String,
    repository: VideoRepository,
    onBack: () -> Unit,
    onExpand: () -> Unit,
    relatedVideoVM: RelatedVideoViewModel = viewModel(
        factory = RelatedVideoViewModelFactory(
            repo = VideoRepository(AppDatabase.getInstance(LocalContext.current).archiveDao())
        )
    )
) {
    val TAG = "VideoPlayerScreen2"
    var currentArchive by remember { mutableStateOf(ArchiveSingleton.archive) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var advertisementState by remember { mutableStateOf(true) }
    val advertisement = AdvertisementData(
        0,
        android.R.drawable.zoom_plate,
        "Midea美的烟灶套餐全家...",
        "https://www.midea.com.cn/",
        "13.7万"
    )
    val tabs = listOf("简介", "评论")
    val pagerState = rememberPagerState(initialPage = 0) { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    var userInfo by remember { mutableStateOf(currentArchive.toUserInfo()) }
    var videoInfo by remember { mutableStateOf(currentArchive.toVideoInfo()) }
    var mid by remember { mutableStateOf(currentArchive.owner.mid) }
    var followers by remember { mutableStateOf(0) }
    var numOfComments by remember { mutableIntStateOf(1024) }

    val relatedUiState by relatedVideoVM.uiState.collectAsState()
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val rawVideoUri = remember {
        RawResourceDataSource.buildRawResourceUri(R.raw.video)
    }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(rawVideoUri) }
    var selectedVideoLabel by remember { mutableStateOf("内置演示视频") }
    var pickerError by remember { mutableStateOf<String?>(null) }

    val openDocumentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (securityException: SecurityException) {
                    Log.w(
                        TAG,
                        "无法获取持久读取权限: ${securityException.message}"
                    )
                }
                selectedVideoUri = uri
                selectedVideoLabel = "本地文件"
                pickerError = null
            } else if (selectedVideoUri == null) {
                pickerError = "请选择一个可播放的视频文件"
            }
        }

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(selectedVideoUri) {
        val uri = selectedVideoUri
        if (uri != null) {
            Log.d(TAG, "加载本地播放链接：$uri")
            exoPlayer.setMediaItem(MediaItem.fromUri(uri))
            exoPlayer.prepare()
        } else {
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
        }
    }

    LaunchedEffect(videoId) {
        errorMessage = null
        try {
            val archive = repository.getArchiveByBvid(videoId.trim())
            if (archive != null) {
                currentArchive = archive
                userInfo = archive.toUserInfo()
                videoInfo = archive.toVideoInfo()
                mid = archive.owner.mid
                followers = repository.getFollowers(mid)
                relatedVideoVM.loadVideos(archive.bvid)
            } else {
                errorMessage = "视频不存在"
            }
        } catch (e: Exception) {
            Log.e(TAG, "加载视频失败: ${e.message}")
            errorMessage = "加载失败: ${e.message}"
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedVideoUri == null) {
                        Text(
                            text = "请选择要播放的视频文件",
                            color = Color.White
                        )
                    } else {
                        VideoPlayerWithCustomTopBar(
                            exoPlayer = exoPlayer,
                            onBack = onBack,
                            onExpand = onExpand
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                VideoSourceSelector(
                    currentLabel = selectedVideoLabel,
                    onPlayRaw = {
                        selectedVideoUri = rawVideoUri
                        selectedVideoLabel = "内置演示视频"
                        pickerError = null
                    },
                    onPickLocal = {
                        pickerError = null
                        openDocumentLauncher.launch(arrayOf("video/*"))
                    }
                )
                if (pickerError != null) {
                    Text(
                        text = pickerError ?: "",
                        color = Color(0xFFFF8A80),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "加载失败",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        fontSize = 12.sp
                    )
                }
            }
        }

        stickyHeader {
            Column {
                if (advertisementState) {
                    AdvertisementPart(advertisement = advertisement)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.White),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = if (index == 1) "评论 $numOfComments" else title,
                                    color = if (pagerState.currentPage == index) Color.Red else Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        DanmuButton(
                            checked = false,
                            onCheckedChange = {}
                        )
                    }
                }
            }
        }

        item {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> {
                        Column {
                            val videoCount = userInfo.videoCount.toDisplayCount()
                            UpInfoBar(
                                avatar = userInfo.avatar.toString(),
                                name = userInfo.name,
                                fans = followers.toDisplayCount(),
                                videoCount = videoCount,
                                isFollowed = true,
                                onChargeClick = {},
                                onFollowClick = {}
                            )
                            VideoHeader(video = currentArchive, videoInfo = videoInfo)
                            when (relatedUiState) {
                                is VideoUiState.Loading -> {
                                    CircularProgressIndicator()
                                }

                                is VideoUiState.Success -> {
                                    val archives = (relatedUiState as VideoUiState.Success).archives
                                    archives.forEach { archive ->
                                        val uiModel = archive.toUiModel()
                                        VideoIntroCard(uiModel) {
                                            rootNavController.navigate("player/${archive.bvid}")
                                        }
                                    }
                                }

                                is VideoUiState.Error -> {
                                    Text(
                                        text = (relatedUiState as VideoUiState.Error).message,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(5) {
                                CommentCard(
                                    avatar = coil.compose.rememberImagePainter(android.R.drawable.ic_menu_camera),
                                    userName = "孙熙然",
                                    level = "6",
                                    isUp = true,
                                    isPinned = it == 0,
                                    time = "2天前 河北",
                                    content = "由于本视频评论区出现了指责我删除评论控制的谣言...",
                                    likeCount = 22000,
                                    replyCount = 231,
                                    onLikeClick = {},
                                    onReplyClick = {},
                                    onMoreClick = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoSourceSelector(
    currentLabel: String,
    onPlayRaw: () -> Unit,
    onPickLocal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "当前视频来源：$currentLabel",
            color = Color.White,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onPlayRaw) {
                Text("播放内置视频")
            }
            Button(onClick = onPickLocal) {
                Text("选择本地视频")
            }
        }
    }
}