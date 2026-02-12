package com.example.dilidiliactivity.ui.Pages.homePage.RelatedVideo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dilidiliactivity.domain.repository.VideoRepository
import com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage.VideoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RelatedVideoViewModel @Inject constructor(
    private val repository: VideoRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState

    fun loadVideos(bvid:String,aid:String = ""){
        viewModelScope.launch {
            try {
                val archives = repository.getRelatedVideo(bvid,aid)
                _uiState.value = VideoUiState.Success(archives)
            } catch (e: Exception) {
                _uiState.value = VideoUiState.Error("加载失败: ${e.message}")
            }
        }
    }
}