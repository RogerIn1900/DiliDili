package com.example.dilidiliactivity.ui.Pages.homePage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.dilidiliactivity.R
import com.example.dilidiliactivity.data.local.HomePageData.HomeTopBarTabs
import com.example.dilidiliactivity.data.local.HomePageData.TabItem
import com.example.dilidiliactivity.data.local.PopularPreciousResponse.PopularPreciousResponse
import com.example.dilidiliactivity.data.remote.ApiClient.RetrofitClient
import com.example.dilidiliactivity.ui.Pages.PopularPreciousPage.PopularPreciousPage
import com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage.AnimatePage
import com.example.dilidiliactivity.ui.Pages.homePage.RandomVideo.RandomVideo
import com.example.dilidiliactivity.ui.Pages.homePage.RelatedVideo.RelatedVideo
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.SharedVideoViewModel
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VerticalPagerExample
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VideoIntro
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VideoViewModel
import com.example.dilidiliactivity.ui.Pages.homePage.YingShiPage.YingShiPage
import com.example.dilidiliactivity.ui.Pages.homePage.YingShiPage.YingShiPage2
import com.example.dilidiliactivity.ui.Pages.homePage.ZuixinPage.ZuixinPage
import com.example.dilidiliactivity.ui.common.TopBars.HomeTopBar
import com.example.dilidiliactivity.ui.common.animatePart.Like
import kotlinx.coroutines.launch
//import kotlin.OptIn


@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class)
@Composable
fun HomePage(paddingValues: PaddingValues, sharedVm: SharedVideoViewModel, rootNavController: NavController) {
    val tabItems = listOf(
        TabItem(title = "推荐"),
        TabItem(title = "长视频")
    )
    
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = {tabItems.size})
//    val systemUiController : SystemUiController= rememberSystemUiController()
    val darkIconsState = remember { mutableStateOf(false) }
    CollapsingHeaderScreen(paddingValues,sharedVm, rootNavController)

//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(paddingValues)
//    ){
//        TabRow(
//            selectedTabIndex = pagerState.currentPage,
//            containerColor = when(pagerState.currentPage){
//                0 -> Color.Magenta
//                1 -> Color.Cyan
//                else -> Color.Cyan
//            },
//            contentColor = when(pagerState.currentPage){
//                0 -> Color.LightGray
//                1 -> Color.Black
//                else -> Color.Cyan
//            }
//        ) {
//            tabItems.forEachIndexed { index, item ->
//                Tab(
//                    text = { Text(text = item.title) },
//                    selected = pagerState.currentPage == index,
//                    onClick = {
//                        coroutineScope.launch{
//                            pagerState.animateScrollToPage(index)
//                        }
//                    }
//                )
//            }
//        }
//        LaunchedEffect(key1 = pagerState.currentPage) {
//            darkIconsState.value = when (pagerState.currentPage) {
//                0 -> false
//                1 -> true
//                else -> false
//            }
//            Log.d("TAG", "HomePage: ${darkIconsState.value}")
////            systemUiController.setSystemBarsColor(
////                color = Color.Transparent,
////                darkIcons = darkIconsState.value
////            )
//        }
//        HorizontalPager(
//            state = pagerState
//        ) {
//            page ->
//            when(page){
//                0 -> {
//                    //获取随机视频
////                    BiliRegionScreen(ps = 3, rid = 1)
//                    RandomVideo()
////                    WebView()
////                    TextField(text = getBUrl(), modifier = Modifier.padding(10.dp))
////                    getBKey()
//                }
//                1 -> {
//                    Text(text = "长视频")
//                    PopularVideo()
//                }
//            }
//        }
//    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CollapsingHeaderScreen(paddingValues: PaddingValues, sharedVm: SharedVideoViewModel, rootNavController: NavController) {
    val onAllCategoriesClick: () -> Unit = {}
    val tabs = listOf("直播","推荐", "热门","动画", "最新", "影视", "新征程")


    // Pager 状态
    val pagerState = rememberPagerState(initialPage = 2) {
        tabs.size // 页数
    }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn (
        modifier = Modifier.padding(paddingValues)
    ){
        // 第一部分：随滚动消失的 Header
        item {
            HomeTopBar()
        }

        // 第二部分：固定 TabRow
        stickyHeader {
            val redColor = Color.Red
            val tabHeight = 48.dp // Row 高度
            val indicatorWidth = 25.dp // 指示器宽度，可根据需要调整

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
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(), // 填满 Row 高度
                    edgePadding = 0.dp,
                    divider = {}, // 去掉底部分割线
                    indicator = { tabPositions ->
                        val currentTabPosition = tabPositions[pagerState.currentPage]
                        TabRowDefaults.Indicator(
                            modifier = Modifier
                                .tabIndicatorOffset(currentTabPosition)
                                .width(indicatorWidth) // 设置指示器宽度
                                .padding(horizontal = ((currentTabPosition.width - indicatorWidth) / 2)) // 居中
                                .padding(top = 8.dp)
                                .height(3.dp), // 高度
                            color = redColor
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            modifier = Modifier.fillMaxHeight() // 填满 Row 高度
                        ) {
                            Text(
                                text = title,
                                color = if (pagerState.currentPage == index) redColor else Color.Gray,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .padding(horizontal = 0.dp) // 内间距，防止太紧
//                                    .align(Alignment.CenterVertically)
                            )
                        }
                    }
                }

                // 固定 "更多" Tab
                Box(
                    modifier = Modifier
//                        .width(40.dp)
//                        .height(48.dp)
                        .clickable { onAllCategoriesClick() },
                    contentAlignment = Alignment.Center
                ) {
                    val isDarkTheme = isSystemInDarkTheme()
                    val imageRes = if (isDarkTheme) R.drawable.more_theme_dark else R.drawable.more_theme_white

                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "主题适配图标",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

        }
        // 第三部分：HorizontalPager，左右滑动切换内容
        item {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .height(800.dp) // 给定高度，否则在 LazyColumn 里没法渲染
            ) { page ->
                when(page){
                    HomeTopBarTabs.Zhibo.page -> {
                        RandomVideo()
//                        PopularPreciousPage()
//                        VerticalPagerExample()
//                        showInfo(HomeTopBarTabs.Zhibo.destination)
                    }
                    HomeTopBarTabs.Tuijian.page -> {
                        AnimatePage(rootNavController = rootNavController)
//                        RandomVideo()
//                        showInfo(HomeTopBarTabs.Tuijian.destination)
                    }
                    HomeTopBarTabs.Remen.page -> {
                        PopularPreciousPage(rootNavController = rootNavController)
//                        VideoIntro() { video ->
//                            sharedVm.currentVideo = video
//                            rootNavController.navigate("player/${video.id}")
//                        }
//                        showInfo(HomeTopBarTabs.Remen.destination)
                    }
                    HomeTopBarTabs.Donghua.page -> {
//                        RelatedVideo(bvid = "BV1X44y1X7jz",rootNavController)
                        RelatedVideo(bvid = "BV1X44y1X7jz",rootNavController = rootNavController)
//                        Like()
//                        showInfo(HomeTopBarTabs.Donghua.destination)
//                        AnimatePage(navController = rootNavController)
                    }
                    HomeTopBarTabs.Zuixin.page -> {
//                        showInfo(HomeTopBarTabs.Zuixin.destination)
                        ZuixinPage()
                    }
                    HomeTopBarTabs.Yingshi.page -> {
                        YingShiPage(Modifier)
//                        showInfo(HomeTopBarTabs.Yingshi.destination)
                    }
                    HomeTopBarTabs.Xinzhengcheng.page -> {
                        showInfo(HomeTopBarTabs.Xinzhengcheng.destination)
                    }
                }



            }
        }
    }

}



@Composable
fun showInfo(msg:String){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            repeat(40) { index ->
                Text(text = "${msg} 内容 $index\n")
            }
        }

    }
}
