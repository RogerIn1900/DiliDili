package com.example.dilidiliactivity.ui.Pages.homePage.RelatedVideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dilidiliactivity.domain.repository.VideoRepository

class RelatedVideoViewModelFactory(
    private val repo: VideoRepository
) : ViewModelProvider.Factory{
    override fun<T : ViewModel> create(modelClass: Class<T>):T{
        if (modelClass.isAssignableFrom(RelatedVideoViewModel::class.java)){
            return RelatedVideoViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}