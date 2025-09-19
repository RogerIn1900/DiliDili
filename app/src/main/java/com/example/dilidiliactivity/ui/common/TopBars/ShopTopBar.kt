package com.example.dilidiliactivity.ui.common.TopBars

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class ShopTopBar {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSwitchOnSecondPart() {
    val listState = rememberLazyListState()

    // 判断第二部分是否可见
    val isSecondPartVisible by remember {
        derivedStateOf {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            // 第二部分在 LazyColumn 中的 index = 1
            visibleItems.any { it.index == 0 }
        }
    }

    Scaffold(
        topBar = {
            if (isSecondPartVisible) {
                TopAppBar(
                    title = { Text("模式 A（第二部分可见时）") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6200EE),
                        titleContentColor = Color.White
                    )
                )
            } else {
                TopAppBar(
                    title = { Text("模式 B（第二部分消失后）") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.DarkGray,
                        titleContentColor = Color.White
                    )
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 第一部分内容
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("内容区域（第一部分）")
                }
            }

            // 第二部分：关键部分（触发切换的依据）
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.Yellow),
                    contentAlignment = Alignment.Center
                ) {
                    Text("第二部分")
                }
            }

            // 其他内容
            items(50) { index ->
                Text(
                    text = "第 $index 行内容",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}
