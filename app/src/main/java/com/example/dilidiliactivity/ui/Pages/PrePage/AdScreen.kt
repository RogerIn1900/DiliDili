package com.example.dilidiliactivity.ui.Pages.PrePage

import android.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AdScreen(
    onAdFinished: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(5) } // 倒计时 5 秒

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        onAdFinished()
    }


    Box(modifier = Modifier.fillMaxSize()) {
        // 广告背景图（可以替换成网络图片）
        Image(
            painter = painterResource(id = R.drawable.bottom_bar),
            contentDescription = "广告",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 右上角跳过按钮
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable { onAdFinished() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("跳过 $timeLeft", color = Color.White)
        }
    }
}
