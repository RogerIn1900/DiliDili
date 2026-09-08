package com.example.dilidiliactivity.ui.pages.homepage.videoplayerpage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun VideoDetailsStatus(state: VideoPlayerUiState, onBack: () -> Unit, onRetry: () -> Unit) {
    Column(Modifier.padding(16.dp)) {
        TextButton(onClick = onBack) { Text("返回") }
        if (state.isLoading || state.errorMessage == null) {
            CircularProgressIndicator()
            Text("正在加载视频")
        } else {
            Text(state.errorMessage)
            Button(onClick = onRetry) { Text("重试") }
        }
    }
}
