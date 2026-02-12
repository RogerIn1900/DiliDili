package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dilidiliactivity.data.local.archive.Archive
import com.example.dilidiliactivity.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    val repository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState

    fun loadVideo(videoId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val archive = repository.getVideoDetail(videoId)
                if (archive == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "视频不存在"
                    )
                } else {
                    val url = getBiliVideoUrl(archive)
                    if (url == null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "无法获取视频源"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            archive = archive,
                            videoUrl = url
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "加载失败: ${e.message}"
                )
            }
        }
    }
}

data class VideoPlayerUiState(
    val archive: Archive? = null,
    val videoUrl: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
