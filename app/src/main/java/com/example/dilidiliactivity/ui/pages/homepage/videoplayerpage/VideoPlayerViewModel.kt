package com.example.dilidiliactivity.ui.pages.homepage.videoplayerpage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dilidiliactivity.data.local.archive.Archive
import com.example.dilidiliactivity.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    val repository: VideoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null
    private var requestGeneration = 0L

    fun loadVideo(videoId: String) {
        val generation = ++requestGeneration
        loadJob?.cancel()
        _uiState.value = VideoPlayerUiState(isLoading = true)
        loadJob = viewModelScope.launch {
            val result = try {
                val archive = repository.getVideoDetail(videoId.trim())
                if (archive == null) {
                    VideoPlayerUiState(errorMessage = "视频不存在")
                } else {
                    val url = repository.getPlayUrl(archive.cid.toString(), archive.bvid)
                    if (url.isNullOrBlank()) {
                        VideoPlayerUiState(errorMessage = "无法获取视频源")
                    } else {
                        VideoPlayerUiState(archive = archive, videoUrl = url)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                VideoPlayerUiState(errorMessage = "加载失败: ${error.message}")
            }
            // A dependency may complete despite cancellation. Only the latest selection may publish.
            if (generation == requestGeneration) _uiState.value = result
        }
    }
}

data class VideoPlayerUiState(
    val archive: Archive? = null,
    val videoUrl: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
