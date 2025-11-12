package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import android.R
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioManager
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.dilidiliactivity.data.local.ArchiveSingleton
import com.example.dilidiliactivity.data.local.ArchiveSingleton.archive
import com.example.dilidiliactivity.data.local.DetailsPageData.VideoInfo
import com.example.dilidiliactivity.data.local.advertisement.AdvertisementData
import com.example.dilidiliactivity.data.local.archive.AppDatabase
import com.example.dilidiliactivity.data.mapper.toDisplayCount
import com.example.dilidiliactivity.data.mapper.toUiModel
import com.example.dilidiliactivity.data.mapper.toUserInfo
import com.example.dilidiliactivity.data.mapper.toVideoInfo
import com.example.dilidiliactivity.data.local.archive.Archive
import com.example.dilidiliactivity.domain.repository.VideoRepository
import com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage.VideoUiState
import com.example.dilidiliactivity.ui.Pages.homePage.RandomVideo.WebView
import com.example.dilidiliactivity.ui.Pages.homePage.RelatedVideo.RelatedVideo
import com.example.dilidiliactivity.ui.Pages.homePage.RelatedVideo.RelatedVideoViewModel
import com.example.dilidiliactivity.ui.Pages.homePage.RelatedVideo.RelatedVideoViewModelFactory
import com.example.dilidiliactivity.ui.common.animatePart.Coin
import com.example.dilidiliactivity.ui.common.animatePart.DisLike
import com.example.dilidiliactivity.ui.common.animatePart.Like
import com.example.dilidiliactivity.ui.common.animatePart.Share
import com.example.dilidiliactivity.ui.common.animatePart.Star
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    rootNavController: NavController,
    videoId: String,
    repository: VideoRepository,
    onBack: () -> Unit,
    onExpand: () -> Unit,
    relatedVideoVM: RelatedVideoViewModel = viewModel (
        factory = RelatedVideoViewModelFactory(
            repo = VideoRepository(AppDatabase.getInstance(LocalContext.current).archiveDao())
        )
    )
) {
    val TAG = "VideoPlayerScreen"
    //视频数据加载
    // 本地懒加载数据作为占位
    var currentArchive by remember {
        mutableStateOf(
            ArchiveSingleton.archive
        )
    }
    var videoUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    //广告
    var advertisementState by remember { mutableStateOf(true) }
    var advertisement = AdvertisementData(
        0,
        R.drawable.zoom_plate,
        "Midea美的烟灶套餐全家...",
        "https://www.midea.com.cn/",
        "13.7万"
    )

    //视频列表与评论切换数据
    var numOfComments by remember { mutableIntStateOf(1024) }
    val tabs = listOf<String>("简介", "评论 ${numOfComments}")
    var pagerState = rememberPagerState(initialPage = 0) {
        tabs.size
    }
    val coroutineScope = rememberCoroutineScope()
    var playUrl :String by remember { mutableStateOf( "") }
    //用户和视频信息
    var userInfo by remember { mutableStateOf(currentArchive.toUserInfo()) }
    var videoInfo by remember { mutableStateOf(currentArchive.toVideoInfo()) }

    var mid by remember { mutableStateOf(currentArchive.owner.mid) }

    var followers by remember { mutableStateOf(0) }
    val relatedUiState by relatedVideoVM.uiState.collectAsState()

    // 异步加载真实数据
    LaunchedEffect(videoId) {
        isLoading = true
        errorMessage = null
        try {
            //通过数据库查询bvid对应的archive数据
            Log.d(TAG,"videoId\n"+videoId.trim())
            var archive = repository.getArchiveByBvid(videoId.trim())
            Log.d(TAG,"archive\n"+archive.toString())

            if (archive != null) {
                currentArchive = archive
                userInfo = archive.toUserInfo() // ⚡ 更新最新的
                Log.d(TAG,"currentArchive：+${currentArchive}")

            } else {
                errorMessage = "视频不存在"
                //重新调用网络接口获取视频信息
//                val newWebData = repository.getPopularPrecious()
            }

            //通过网络获取视频数据，但是没有相关的接口
//             archive = repository.getArchiveByBvid(videoId)
//                ?: repository.getVideoDetail(videoId) // 网络接口
            Log.d(TAG,"通过archive内容获取playUrl")
            val BASE_PLAY_URL = "https://player.bilibili.com/player.html"
            val bvid = currentArchive.bvid
            val aid = currentArchive.aid
            val cid = currentArchive.cid
            //获取mid ---》 获取fans数
            mid = currentArchive.owner.mid
            followers = repository.getFollowers(mid)
            val page = 1
            val mid = currentArchive.owner.mid
            playUrl = "$BASE_PLAY_URL?aid=$aid&cid=$cid&page=$page"
            Log.d(TAG,"bvid：${bvid}")
            Log.d(TAG,"aid：${aid}")
            Log.d(TAG,"cid：${cid}")
            Log.d(TAG,"mid：${mid}")

            Log.d(TAG,"playUrl：${playUrl}")
            Log.d(TAG,"playUrl_watch_place1：${playUrl}")
//            userInfo = currentArchive.toUserInfo()
            videoInfo = currentArchive.toVideoInfo()

            Log.d(TAG,"videoInfo：${videoInfo}")
            Log.d(TAG,"userInfor：${userInfo}")

            //更新相关推荐视频
            relatedVideoVM.loadVideos(bvid)

        } catch (e: Exception) {
            errorMessage = "加载失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    Log.d(TAG,"playUrl_watch_place2：${playUrl}")

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // 加载视频详情 + 返回拓展按钮
        item {
            Log.d(TAG,"playUrl_watch_place3：${playUrl}")

            VideoPlayByWebView(
                playUrl = playUrl,
                onBack = onBack,
                onExpand = onExpand
            )
        }
        //广告+简介评论的模式切换
        stickyHeader {
            Column(

            ) {
                if (advertisementState) {
                    AdvertisementPart(advertisement = advertisement)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.White)
                        .border(1.dp, Color.LightGray),
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
                                modifier = Modifier
                                    .background(Color.White)
                                    .fillMaxHeight()
                            ) {
                                Text(
                                    text = title,
                                    color = if (pagerState.currentPage == index) Color.Red else Color.Gray,
                                    fontSize = 12.sp,
                                )
                            }
                        }
//                        Spacer(modifier = Modifier.width(20.dp))

//                        Box(
//                            modifier = Modifier.fillMaxHeight()
//                        ){
                        DanmuButton(
                            checked = false,
                            onCheckedChange = {}
                        )
//                        }
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
                        // 其他视频列表
                        Column {
                            val videoCount = currentArchive.toUserInfo().videoCount.toDisplayCount()
                            Log.d(TAG,"videoCount：${videoCount}")
                            //up主信息
                            UpInfoBar(
                                avatar = userInfo.avatar.toString(),
                                name = userInfo.name,
                                fans = followers.toDisplayCount(),
                                videoCount = videoCount,
                                isFollowed = true,
                                onChargeClick = {},
                                onFollowClick = {}
                            )
                            //视频标题信息
                            //视频点赞、不喜欢、投币、收藏、转发
                            VideoHeader(currentArchive,videoInfo)
//                            PopularPreciousPageInLazyColumn(rootNavController = rootNavController)
//                            RelatedVideo(bvid = currentArchive.bvid,rootNavController = rootNavController)
                            when(relatedUiState){
                                is VideoUiState.Loading -> {
                                    CircularProgressIndicator()
                                }
                                is VideoUiState.Success -> {
                                    val archives = (relatedUiState as VideoUiState.Success).archives
                                    Log.d(TAG,"archives：${archives}")
                                    archives.forEach { archive ->
                                        val uiModel = archive.toUiModel()
                                        VideoIntroCard(uiModel, onVideoClick = {
                                            rootNavController.navigate("player/${archive.bvid}")
                                        })
                                    }
                                }
                                is VideoUiState.Error -> {
                                    Text((relatedUiState as VideoUiState.Error).message)
                                }
                            }
                        }
                    }

                    1 -> {
                        Column {
                            for (i in 1..10) {
                                // 评论列表
                                CommentCard(
                                    avatar = painterResource(id = R.drawable.ic_menu_add),
                                    userName = "孙熙然",
                                    level = "6",
                                    isUp = true,
                                    isPinned = true,
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
fun UpInfoBar(
    avatar: String,
    name: String,
    fans: String,
    videoCount: String,
    isFollowed: Boolean,
    onChargeClick: () -> Unit,
    onFollowClick: () -> Unit
) {



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：头像 + 信息
        AsyncImage(
            model = avatar,
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(64.dp)   // 控制大小
                .clip(CircleShape) // 圆形头像
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = Color(0xFFFF6699), // 粉色
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
//                modifier = Modifier.
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$fans 粉丝    $videoCount 视频",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))


        // 右侧按钮：充电
        OutlinedButton(
            onClick = onChargeClick,
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color(0xFFFF6699)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFFF6699)
            ),
            modifier = Modifier.height(34.dp)
                .width(80.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build, // ⚡
                contentDescription = null,
                tint = Color(0xFFFF6699),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "充电", fontSize = 14.sp,
                modifier = Modifier,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 右侧按钮：已关注
        FollowButton()
//        Button(
//            onClick = onFollowClick,
//            shape = RoundedCornerShape(50),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color(0xFF2C2C2E), // 深灰色背景
//                contentColor = Color.Gray
//            ),
//            modifier = Modifier.height(34.dp)
//                .width(80.dp),
//            contentPadding = PaddingValues(0.dp)
//
//        ) {
//            Icon(
//                imageVector = Icons.Default.Menu,
//                contentDescription = null,
//                tint = Color.Gray,
//                modifier = Modifier.size(16.dp)
//            )
//            Spacer(modifier = Modifier.width(4.dp))
//            Text("已关注", fontSize = 14.sp)
//        }
    }

}

@Composable
fun FollowButton() {
    var isFollowed by remember { mutableStateOf(false) }

    Button(
        onClick = { isFollowed = !isFollowed }, // 点击切换状态
        shape = RoundedCornerShape(50),
        colors = if (isFollowed) {
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2C2C2E), // 深灰背景
                contentColor = Color.Gray
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD46A8E), // 粉色背景
                contentColor = Color.White
            )
        },
        modifier = Modifier
            .height(34.dp)
            .width(90.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isFollowed) {
            // 已关注状态
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("已关注", fontSize = 14.sp)
        } else {
            // 未关注状态
            Icon(
                imageVector = Icons.Default.Add, // “+” 图标
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("关注", fontSize = 14.sp)
        }
    }
}

private enum class PlayerGestureMode {
    Brightness,
    Volume,
    Seek
}

private const val SEEK_SENSITIVITY_MS = 60_000f
private const val THREE_X_SPEED = 3f
private const val MIN_BRIGHTNESS = 0.05f

private fun Context.findActivity(): Activity? {
    var current: Context? = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    return current as? Activity
}

private fun getInitialBrightness(activity: Activity?): Float {
    activity ?: return 0.5f
    val layoutParams = activity.window.attributes
    val current = layoutParams.screenBrightness
    if (current in 0f..1f) {
        return current
    }
    return runCatching {
        val systemValue = Settings.System.getInt(
            activity.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        )
        systemValue / 255f
    }.getOrDefault(0.5f)
}

private fun setActivityBrightness(activity: Activity?, value: Float) {
    activity ?: return
    val params = activity.window.attributes
    params.screenBrightness = value.coerceIn(MIN_BRIGHTNESS, 1f)
    activity.window.attributes = params
}



@Composable
fun CommentCard2(
    avatar: Painter,
    userName: String,
    level: String,
    isUp: Boolean,
    isPinned: Boolean,
    time: String,
    content: String,
    likeCount: Int,
    replyCount: Int,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C1E))
            .padding(12.dp)
    ) {
        // 顶部：头像
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = avatar,
                contentDescription = "avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                // 名字 + 等级 + UP
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Lv$level",
                        color = Color(0xFF9CDCFE),
                        fontSize = 12.sp,
                        modifier = Modifier
                            .background(Color(0x3329B6F6), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                    if (isUp) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "UP",
                            color = Color(0xFFFF6699),
                            fontSize = 12.sp,
                            modifier = Modifier
                                .background(Color(0x33FF6699), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                //置顶
                Row {
                    if (isPinned) {
                        Text(
                            text = "置顶 ",
                            color = Color(0xFFFFC107),
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = time,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 正文内容
        Text(
            text = content,
            color = Color.White,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 底部操作栏
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "like",
                    tint = Color.Gray
                )
            }
            Text(
                text = likeCount.toString(),
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.width(16.dp))

            TextButton(onClick = onReplyClick) {
                Text("回复", color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "more",
                    tint = Color.Gray
                )
            }
        }
    }
}


@Composable
fun DanmuButton(
    text: String = "点我发弹幕",
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50)) // 圆角矩形
            .border(1.dp, Color.DarkGray, RoundedCornerShape(50))
            .background(Color(0xFF1C1C1E)) // 背景色
            .clickable { onCheckedChange(!checked) }
            .height(40.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        // 左边文字
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier
                .width(80.dp)

        )

        // 右边图标按钮
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp))
                .background(Color(0xFF2C2C2E)),
            contentAlignment = Alignment.Center
        ) {
            // 模拟电视机+“弹”字的组合，你可以换成真正的 Icon
            Text("弹", color = Color.Gray)

            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check, // Material 自带 ✔
                    contentDescription = null,
                    tint = Color(0xFFFF6699), // 粉色
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp) // 微调位置
                        .size(14.dp)
                )
            }
        }
    }
}


@Composable
fun AdvertisementPart(advertisement: AdvertisementData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberImagePainter(advertisement.pic),
            contentDescription = null,
            modifier = Modifier
                .height(40.dp)
                .width(60.dp)
                .padding(8.dp)
        )
        Column {
            Text(text = advertisement.title)
            Text(text = advertisement.count + "播放")
        }
        Spacer(modifier = Modifier.weight(1.0f))
        Icon(Icons.Default.MoreVert, contentDescription = null)
    }
}

@Composable
fun PopularPreciousPageInLazyColumn(
    rootNavController: NavController,
    viewModel: VideoViewModel = viewModel(),
    onVideoClick: (Archive) -> Unit = { archive ->
        rootNavController.navigate("player/${archive.bvid}")
    }
) {
    val state by viewModel.preciousState.collectAsState()

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
            val list = state!!.data.list
            Column {
                list.forEach {
                    val uiModel = archive.toUiModel()  // 调用前面写的 mapper
                    VideoIntroCard(
                        video = uiModel,
                        onVideoClick = { onVideoClick(archive) }
                    )
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoHeader(video: Archive,videoInfo: VideoInfo) {
    Log.d("VideoHeader", "video: $video")
    val videoInfo = video.toVideoInfo()
    Log.d("VideoHeader", "videoInfo: $videoInfo")

    var like by remember { mutableStateOf(videoInfo.likes) }
    var liked by remember { mutableStateOf(false) }

    var dislike by remember { mutableStateOf(1024) }
    var disliked by remember { mutableStateOf(false) }

    var coin by remember { mutableStateOf(videoInfo.coins) }
    var coined by remember { mutableStateOf(false) }

    var star by remember { mutableStateOf(videoInfo.favorites) }
    var starred by remember { mutableStateOf(false) }

    var share by remember { mutableStateOf(videoInfo.shares) }
    var shared by remember { mutableStateOf(false) }

    //video变化的时候数据也跟着变化
    LaunchedEffect(video) {
        like = videoInfo.likes
        coin = videoInfo.coins
        star = videoInfo.favorites
        share = videoInfo.shares
    }

    //标题
    Text(
        text = video.title,
        color = Color.Gray,
        fontSize = 18.sp,
        modifier = Modifier.padding(horizontal = 12.dp)
    )
    //描述
    Text(
        text = "${video.owner.name} • ${video.stat.view.toDisplayCount()}播放量",
        color = Color.Gray,
        fontSize = 14.sp,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))

    // 互动按钮
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Like(like) { clicked ->
            like += if (clicked) 1 else -1
        }
        DisLike(dislike) { newCount ->
            dislike += if (newCount) 1 else -1

        }
        Coin(coin) { newCount ->
            coin += if (newCount) 1 else -1

        }
        Star(star) { newCount ->
            star += if (newCount) 1 else -1

        }
        Share(share) { newCount ->
            share += if (newCount) 1 else -1

        }
    }

}

@Composable
fun VideoPlayByWebView(
    playUrl: String,
    onBack: () -> Unit,
    onExpand: () -> Unit
) {
    var showTopBar by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        WebView(playUrl)

        if (showTopBar) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x55000000))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.White
                    )
                }
                IconButton(onClick = onExpand) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


// ========================= VideoDetailPage =========================
@Composable
fun VideoDetailPage(
    video: Archive,
    videoUrl: String,
    onBack: () -> Unit,
    onExpand: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) { onDispose { exoPlayer.release() } }


    Scaffold(
        topBar = { VideoPlayerWithCustomTopBar(exoPlayer, onBack, onExpand) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.Black)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = video.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Text(
                    text = "${video.owner.name} • ${video.stat.view}播放量",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 互动按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
//                    IconButton(onClick = { /* 点赞逻辑 */ }) {
//                        Icon(Icons.Default.ThumbUp, contentDescription = "点赞", tint = Color.White)
//                    }
                    Like(
                        count = 1024,
                        onToggle = {

                        }
                    )
                    IconButton(onClick = { /* 不喜欢逻辑 */ }) {
                        Icon(Icons.Default.Close, contentDescription = "不喜欢", tint = Color.White)
                    }
                    IconButton(onClick = { /* 收藏逻辑 */ }) {
                        Icon(Icons.Default.Star, contentDescription = "收藏", tint = Color.White)
                    }
                    IconButton(onClick = { /* 分享逻辑 */ }) {
                        Icon(Icons.Default.Share, contentDescription = "分享", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 推荐视频列表
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(10) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp, 70.dp)
                                    .background(Color.DarkGray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("推荐视频标题 $index", color = Color.White)
                                Text("作者 • 播放量", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun VideoPlayerWithCustomTopBar(
    exoPlayer: ExoPlayer,
    onBack: () -> Unit,
    onExpand: () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val activity = remember { context.findActivity() }
    val audioManager = remember {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }
    val maxVolume = remember(audioManager) {
        audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)?.coerceAtLeast(1) ?: 1
    }
    var currentVolume by remember {
        mutableIntStateOf(audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0)
    }
    var currentBrightness by remember { mutableStateOf(getInitialBrightness(activity)) }
    var showTopBar by remember { mutableStateOf(true) }
    var isSpeedBoosted by remember { mutableStateOf(false) }
    var normalPlaybackParameters by remember { mutableStateOf(exoPlayer.playbackParameters) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    var dragMode by remember { mutableStateOf<PlayerGestureMode?>(null) }
    var accumulatedDx by remember { mutableStateOf(0f) }
    var accumulatedDy by remember { mutableStateOf(0f) }
    var initialSeekPosition by remember { mutableStateOf(0L) }
    val gestureThresholdPx = remember(density) { with(density) { 16.dp.toPx() } }

    fun applyBrightness(target: Float) {
        val clamped = target.coerceIn(MIN_BRIGHTNESS, 1f)
        currentBrightness = clamped
        setActivityBrightness(activity, clamped)
    }

    fun applyVolume(target: Int) {
        audioManager?.let { manager ->
            val clamped = target.coerceIn(0, maxVolume)
            manager.setStreamVolume(AudioManager.STREAM_MUSIC, clamped, 0)
            currentVolume = clamped
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    setControllerVisibilityListener(object :
                        PlayerView.ControllerVisibilityListener {
                        override fun onVisibilityChanged(visibility: Int) {
                            showTopBar = visibility == View.VISIBLE
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { containerSize = it }
                .pointerInput(exoPlayer) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        },
                        onLongPress = {
                            if (!isSpeedBoosted) {
                                normalPlaybackParameters = exoPlayer.playbackParameters
                                exoPlayer.playbackParameters =
                                    PlaybackParameters(THREE_X_SPEED, normalPlaybackParameters.pitch)
                                isSpeedBoosted = true
                            }
                        },
                        onPress = {
                            try {
                                tryAwaitRelease()
                            } finally {
                                if (isSpeedBoosted) {
                                    exoPlayer.playbackParameters = normalPlaybackParameters
                                    isSpeedBoosted = false
                                }
                            }
                        }
                    )
                }
                .pointerInput(containerSize) {
                    if (containerSize == IntSize.Zero) return@pointerInput
                    detectDragGestures(
                        onDragStart = {
                            dragMode = null
                            accumulatedDx = 0f
                            accumulatedDy = 0f
                            initialSeekPosition = exoPlayer.currentPosition
                        },
                        onDrag = { change, dragAmount ->
                            accumulatedDx += dragAmount.x
                            accumulatedDy += dragAmount.y

                            if (dragMode == null) {
                                val absDx = abs(accumulatedDx)
                                val absDy = abs(accumulatedDy)
                                if (absDx > gestureThresholdPx || absDy > gestureThresholdPx) {
                                    dragMode = if (absDx > absDy) {
                                        PlayerGestureMode.Seek
                                    } else {
                                        if (change.position.x < containerSize.width / 2f) {
                                            PlayerGestureMode.Brightness
                                        } else {
                                            PlayerGestureMode.Volume
                                        }
                                    }
                                }
                            }

                            when (dragMode) {
                                PlayerGestureMode.Seek -> {
                                    val duration = exoPlayer.duration.takeIf { it > 0 } ?: Long.MAX_VALUE
                                    val deltaMs = (accumulatedDx / containerSize.width) * SEEK_SENSITIVITY_MS
                                    val target = (initialSeekPosition + deltaMs).coerceIn(
                                        0f,
                                        if (duration == Long.MAX_VALUE) Float.MAX_VALUE else duration.toFloat()
                                    ).toLong()
                                    exoPlayer.seekTo(target)
                                }

                                PlayerGestureMode.Brightness -> {
                                    if (containerSize.height > 0) {
                                        val delta = -dragAmount.y / containerSize.height
                                        applyBrightness(currentBrightness + delta)
                                    }
                                }

                                PlayerGestureMode.Volume -> {
                                    if (containerSize.height > 0) {
                                        val step = (-dragAmount.y / containerSize.height) * maxVolume
                                        applyVolume(currentVolume + step.roundToInt())
                                    }
                                }

                                null -> Unit
                            }

                            change.consume()
                        },
                        onDragEnd = {
                            dragMode = null
                            accumulatedDx = 0f
                            accumulatedDy = 0f
                        },
                        onDragCancel = {
                            dragMode = null
                            accumulatedDx = 0f
                            accumulatedDy = 0f
                        }
                    )
                }
        )

        if (showTopBar) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x55000000))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "返回",
                        tint = Color.White
                    )
                }
                IconButton(onClick = onExpand) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "更多",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

suspend fun getBiliVideoUrl(archive: Archive): String? = withContext(Dispatchers.IO) {
    val client = OkHttpClient()
    val cid = archive.cid
    val bvid = archive.bvid
    val apiUrl = "https://api.bilibili.com/x/player/playurl?cid=$cid&bvid=$bvid&qn=80&otype=json"

    try {
        val request = Request.Builder().url(apiUrl).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext null
            val body = response.body?.string() ?: return@withContext null
            val json = JSONObject(body)
            val durl = json.getJSONObject("data").optJSONArray("durl")
            if (durl != null && durl.length() > 0) {
                durl.getJSONObject(0).getString("url")
            } else null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}


@Composable
fun NetworkErrorScreenWithPlayer(
    videoUrl: String = "",
    onRetry: () -> Unit = {}
) {
    val context = LocalContext.current

    // 创建 ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 顶部栏
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 🎬 视频播放器区域，固定 16:9
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f) // 保持视频区域大小
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 中间错误提示内容
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Error Icon",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "网络尚未连接，请稍后再试",
                color = Color.Gray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE06287)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Text("再试一次", color = Color.White)
            }
        }
    }

    // 释放资源
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}


@Preview
@Composable
fun NetworkErrorScreenPreview() {
    NetworkErrorScreenWithPlayer()
}