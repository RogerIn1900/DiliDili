package com.example.dilidiliactivity.ui.Pages.homePage.recommend

import androidx.compose.ui.draw.clip


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

class RandomVideoPart {
}

@Composable
fun BiliVideoCard(
    title: String,
    upName: String,
    bvid: String,
    coverUrl: String,
    shortLink: String
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                // 点击跳转到视频
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(shortLink))
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            // 封面图
            Image(
                painter = rememberAsyncImagePainter(coverUrl),
                contentDescription = "封面图",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "UP主: $upName", style = MaterialTheme.typography.bodyMedium)
            Text(text = "BV号: $bvid", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
