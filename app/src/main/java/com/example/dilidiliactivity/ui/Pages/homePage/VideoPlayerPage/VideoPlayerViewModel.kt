package com.example.dilidiliactivity.ui.Pages.homePage.VideoPlayerPage

import androidx.lifecycle.ViewModel
import com.example.dilidiliactivity.data.local.VideoPlayerData.Video
import kotlinx.coroutines.delay
import androidx.lifecycle.viewModelScope
import com.example.dilidiliactivity.data.local.archive.Archive
import com.example.dilidiliactivity.domain.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VideoPlayerViewModel : ViewModel() {
    // 模拟网络请求
    suspend fun fetchVideoDetail(videoId: String): Video {
        delay(1000) // 假装网络延迟
        return Video(
            id = videoId,
            title = "最新标题（已刷新）",
            coverUrl = "http://example.com/cover_updated.jpg",
            views = "999万",
            danmu = "30万",
            date = "2025-09-03",
            duration = "15:40",
            videoUrl = "http://example.com/video$videoId.mp4"
        )
    }
}



class VideoPlayerViewModel2(
    private val repository: VideoRepository
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
