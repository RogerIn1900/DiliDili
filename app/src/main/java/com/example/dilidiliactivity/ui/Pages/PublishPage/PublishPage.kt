package com.example.dilidiliactivity.ui.Pages.PublishPage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.dilidiliactivity.data.local.HomePageData.TabItem
import kotlinx.coroutines.launch


@Composable
fun PublishPage(paddingValues: PaddingValues) {

    val tabItems = listOf(
        TabItem(title = "全部"),
        TabItem(title = "视频"),
        TabItem(title = "图片")
    )

    val pagerState = rememberPagerState(pageCount = { tabItems.size })
    val coroutineScope = rememberCoroutineScope ()

//        Text(text = "frendsPage", modifier = Modifier.padding(paddingValues))


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        TabRow(selectedTabIndex = pagerState.currentPage) {

            tabItems.forEachIndexed { index, item ->
                Tab(
                    text = { Text(text = item.title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch{pagerState.animateScrollToPage(page = index)}
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState
        ) {
                page ->
            Text(text = tabItems[page].title, modifier = Modifier.fillMaxSize())

        }
    }
}