package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dilidiliactivity.data.local.VideoPlayerData.VideoIntroUiModel
import com.example.dilidiliactivity.R

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
