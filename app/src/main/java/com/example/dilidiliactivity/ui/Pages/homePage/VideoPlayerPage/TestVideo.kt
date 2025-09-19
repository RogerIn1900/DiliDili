package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.dilidiliactivity.data.local.VideoPlayerData.Video
import com.example.dilidiliactivity.data.local.VideoPlayerData.VideoIntroUiModel
import com.example.dilidiliactivity.R

//https://player.bilibili.com/player.html?aid=115095073986538&cid=31957584158&page=1
//https://player.bilibili.com/player.html?aid=115073162937721&cid=31878152237&page=1

@Composable
fun VideoIntro(
    onVideoClick: (Video) -> Unit
){
    val pagerState = rememberPagerState(pageCount = {5})
//    val pagerState = rememberPagerState(pageCount = { 5 }) // 5 个页面
    val infos = listOf<String>(
        "第 1 页",
        "第 2 页",
        "第 3 页",
        "第 4 页",
        "第 5 页",
        "第 5 页",
        "第 5 页",
        "第 5 页",
        "第 5 页",
        "第 5 页",
        "第 5 页",
        "第 5 页"
    )
    val videos = listOf(
        Video("1", "标题1", "http://example.com/cover1.jpg", "100万", "2万", "2025-09-01", "12:30", "http://example.com/video1.mp4"),
        Video("2", "标题2", "http://example.com/cover2.jpg", "50万", "1万", "2025-08-30", "8:20", "http://example.com/video2.mp4")
    )

    LazyColumn(
//        state = pagerState,
        modifier = Modifier.wrapContentSize()

    ) {
//        infos.forEach { info ->
//            item {
//                VideoIntroCard()
//            }
//        }
        items(videos){video ->
            VideoItem(video){
                onVideoClick(video)
            }

        }
    }
}


@Composable
fun VideoItem(video: Video, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(video.coverUrl),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(video.title, maxLines = 2)
            Text("播放量: ${video.views.toInt()} · 弹幕: ${video.danmu}")
            Text("时长: ${video.duration} · 日期: ${video.date}")
        }
    }
}

@Composable
fun VideoIntroCard(
    video: VideoIntroUiModel,
    onVideoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(16f / 4f)
            .clickable { onVideoClick() }
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(5.dp)
        ) {
            AsyncImage(   // 用 Coil 显示网络封面图
                model = video.pic,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = video.period,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title,
                fontSize = 16.sp,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                video.tips.forEach {
                    if (it.second) {
                        Text(
                            text = it.first,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            // 作者信息
            Row {
                Text(
                    text = video.author,
                    fontSize = 12.sp
                )
            }

            Row {
                Text(
                    text = "${video.viewed} - ${video.time}",
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.more_theme_white),
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun VideoIntroCard_regin(
//    video: Video,
//    onVideoClick: (Video) -> Unit,
//    pic : String= "",
//    period: String = "39:16",
//    title: String= "如何把北极的冰块运到赤道？「神奇组织37」",
//    tips: List<Pair<String,Boolean>> = listOf<Pair<String,Boolean>>(
//        "8万点赞" to true,
//        "百万播放" to false
//    ),
//    author: String= "小约翰可汗",
//    viewed: String= "68.9万观看",
//    time: String = "3小时前",

    pic : String= "",
    period: String = "39:16",
    title: String= "如何把北极的冰块运到赤道？「神奇组织37」",
    tips: List<Pair<String,Boolean>> = listOf<Pair<String,Boolean>>(
        "8万点赞" to true,
        "百万播放" to false
    ),
    author: String= "小约翰可汗",
    viewed: String= "68.9万观看",
    time: String = "3小时前",

) {
//    val pic = ""
//    val period = "39:16"
//    val title = "如何把北极的冰块运到赤道？「神奇组织37」"
//    val tips = listOf<Pair<String,Boolean>>(
//        "8万点赞" to true,
//        "百万播放" to false
//    )
//    val author = "小约翰可汗"
//    val viewed = "68.9万观看"
//    val time = "3小时前"

    Row(
        modifier = Modifier.padding(8.dp)
            .aspectRatio(16f/4f)
    ) {
        Box(
            modifier = Modifier.weight(1f)
                .padding(16.dp)
        ){
            Image(
                painter = painterResource(R.drawable.more_theme_white),
                contentDescription = " ",
                modifier = Modifier.clip(shape = RoundedCornerShape(12.dp))
                    .fillMaxSize()
            )
            Text(
                text = "$period",
                fontSize = 12.sp,
                //调整到右下角
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 16.sp,
                maxLines = 2,
//                fontWeight = 600,
                modifier = Modifier.fillMaxWidth()
            )
            Row (
                modifier = Modifier.fillMaxWidth()
//                    .height(24.dp)
            ){
                tips.forEach {
                    if(it.second)
                    Text(
                        text = it.first,
                        fontSize = 12.sp,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Row {
                Image(
                    painter = painterResource(R.drawable.more_theme_white),
                    contentDescription = " ",
                    modifier = Modifier.size(4.dp)
                )
                Text(
                    text = "$author",
                    fontSize = 12.sp,
//                    modifier = Modifier.padding(4.dp)
                )
            }
            Row {
                Image(
                    painter = painterResource(R.drawable.more_theme_white),
                    contentDescription = " ",
                    modifier = Modifier.size(4.dp)

                )
                Text(
                    text = "$viewed - $time",
                    fontSize = 12.sp,
//                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(R.drawable.more_theme_white),
                    contentDescription = " ",
                    modifier = Modifier.clickable(onClick = {  })
                        .size(10.dp)
                )
            }
        }
    }
}


@OptIn(UnstableApi::class)
@Composable
fun VerticalPagerExample() {
    // pagerState 负责管理当前页数、偏移等
    val pagerState = rememberPagerState(pageCount = { 5 }) // 5 个页面
    val TAG = "VerticalPagerExample"

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        // 每一页内容
        val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan)
        val context = LocalContext.current
        val uri = "https://player.bilibili.com/player.html?aid=115095073986538&cid=31957584158&page=1"
        var isFirstFrameLoad = remember { false }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors[page]),
            contentAlignment = Alignment.Center
        ) {
            Log.d(TAG,"Before enter ExoPlayer")
            ExoPlayer.Builder(context).build().apply {
                Log.d(TAG,"After enter ExoPlayer")
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                repeatMode = REPEAT_MODE_ONE
                setMediaItem(MediaItem.fromUri(uri))
                playWhenReady = true
                prepare()
                addListener(object : Player.Listener {
                    override fun onRenderedFirstFrame() {
                        super.onRenderedFirstFrame()
                        isFirstFrameLoad = true
                        Log.d(TAG, "onRenderedFirstFrame: $isFirstFrameLoad")
                        //thumbnail = thumbnail.copy(second = false)
                    }
                })
            }
        }
    }
}