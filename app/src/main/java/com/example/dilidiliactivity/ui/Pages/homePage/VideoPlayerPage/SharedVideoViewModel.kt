package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dilidiliactivity.data.local.VideoPlayerData.Video

class SharedVideoViewModel : ViewModel() {
    var currentVideo by mutableStateOf<Video?>(null)
}