package com.example.dilidiliactivity.ui.common.TopBars

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dilidiliactivity.data.local.HomePageData.HomeTopBarTabs
import com.example.dilidiliactivity.R
import com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage.AnimatePage
import com.example.dilidiliactivity.ui.Pages.homePage.RandomVideo.RandomVideo
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.SharedVideoViewModel
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VerticalPagerExample
import com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage.VideoIntro
import kotlinx.coroutines.launch



@Composable
fun HomeTopBar() {
    var query by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(start = 16.dp)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.visibility_icon),
            contentDescription = "Left Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .clickable{
                    //跳转到MinePage
                }
        )

        Spacer(modifier = Modifier.width(8.dp))
        // 中间搜索框
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.DarkGray)
                .clickable {
                    //跳转到搜索页面
                },
            contentAlignment = Alignment.CenterStart
        ) {
            var query by remember { mutableStateOf("") }

            BasicTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 14.sp
                ),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.DarkGray)
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                        innerTextField() // 真正的输入框内容
                    }
                }
            )


        }

        Spacer(modifier = Modifier.width(16.dp))

        // 右侧头像（带红点）
        Box(
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    //跳转到游戏页面
                },
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = painterResource(id =R.drawable.stadia_controller_icon), // 替换成你的头像资源
                contentDescription = "Right Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            // 红点
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Red, CircleShape)
                    .align(Alignment.TopEnd)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 消息按钮
        Image(
            painter =  painterResource(id = R.drawable.mail_icon),
            contentDescription = "Message",
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    //跳转到消息页面
                }
        )

    }
}
