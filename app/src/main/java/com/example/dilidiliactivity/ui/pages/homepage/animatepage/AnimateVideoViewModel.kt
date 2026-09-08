package com.example.dilidiliactivity.ui.pages.homepage.animatepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dilidiliactivity.data.local.archive.Archive
import com.example.dilidiliactivity.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class VideoUiState {
    object Loading : VideoUiState()
    data class Success(val archives: List<Archive>) : VideoUiState()
    data class Error(val message: String) : VideoUiState()
}

@HiltViewModel
class AnimateVideoViewModel @Inject constructor(
    private val repo: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<VideoUiState>(VideoUiState.Loading)
    val uiState: StateFlow<VideoUiState> = _uiState
    
    // 累积的列表，保留所有已加载的视频
    private val _allArchives = MutableStateFlow<List<Archive>>(emptyList())
    val allArchives: StateFlow<List<Archive>> = _allArchives
    
    // 刷新状态
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private var observation: kotlinx.coroutines.Job? = null
    private var refresh: kotlinx.coroutines.Job? = null
    private var observedRegion: Int? = null

    fun loadVideos(ps: Int = DEFAULT_PAGE_SIZE, rid: Int = DEFAULT_REGION_ID) {
        if (observedRegion != rid) {
            observation?.cancel()
            refresh?.cancel()
            observedRegion = rid
            _allArchives.value = emptyList()
            observation = viewModelScope.launch {
                repo.observeRegion(rid).collect { rows ->
                    _allArchives.value = rows
                    _uiState.value = VideoUiState.Success(rows)
                }
            }
        }
        refreshVideos(ps, rid)
    }

    fun refreshVideos(ps: Int = DEFAULT_PAGE_SIZE, rid: Int = DEFAULT_REGION_ID) {
        if (refresh?.isActive == true) return
        refresh = viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repo.getVideoList(ps, rid)
                // UI data is exclusively emitted by Room, not by this request result.
                _uiState.value = VideoUiState.Success(_allArchives.value)
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.value = VideoUiState.Error("刷新失败: ${error.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    companion object {
        // Existing region API defaults; this endpoint exposes a snapshot, not a verified cursor contract.
        const val DEFAULT_PAGE_SIZE = 10
        const val DEFAULT_REGION_ID = 1
    }
}
