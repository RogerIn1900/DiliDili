package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dilidiliactivity.domain.repository.VideoRepository

class VideoPlayerViewModelFactory(
    private val repository: VideoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VideoPlayerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VideoPlayerViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
