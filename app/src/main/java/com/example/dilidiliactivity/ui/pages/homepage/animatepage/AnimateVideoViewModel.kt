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

    fun loadVideos(ps: Int = 10, rid: Int = 1) {
        viewModelScope.launch {
            _uiState.value = VideoUiState.Loading
            try {
                val archives = repo.getVideoList(ps, rid)
                // 初始加载时，直接设置列表
                _allArchives.value = archives
                _uiState.value = VideoUiState.Success(_allArchives.value)
            } catch (e: Exception) {
                _uiState.value = VideoUiState.Error("加载失败: ${e.message}")
            }
        }
    }
    
    // 刷新方法：将新数据添加到列表顶部
    fun refreshVideos(ps: Int = 10, rid: Int = 1) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val newArchives = repo.getVideoList(ps, rid)
                // 将新数据添加到现有列表的顶部，并去重（基于 bvid）
                val existingBvids = _allArchives.value.map { it.bvid }.toSet()
                val uniqueNewArchives = newArchives.filter { it.bvid !in existingBvids }
                _allArchives.value = uniqueNewArchives + _allArchives.value
                _uiState.value = VideoUiState.Success(_allArchives.value)
            } catch (e: Exception) {
                _uiState.value = VideoUiState.Error("刷新失败: ${e.message}")
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}