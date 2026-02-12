package com.example.dilidiliactivity.ui.pages.popularpreciouspage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dilidiliactivity.data.local.PopularPreciousResponse.PopularPreciousResponse
import com.example.dilidiliactivity.data.local.archive.ArchiveDao
import com.example.dilidiliactivity.data.local.archive.toEntity
import com.example.dilidiliactivity.data.remote.api.BilibiliApi
import com.example.dilidiliactivity.data.local.archive.Archive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PopularPreciousViewModel @Inject constructor(
    private val dao: ArchiveDao,
    private val api: BilibiliApi
): ViewModel() {

    private var _preciousState = MutableStateFlow<PopularPreciousResponse?>(null)
    val preciousState: StateFlow<PopularPreciousResponse?> = _preciousState

    private val _list = MutableStateFlow<List<Archive>>(emptyList())
    val list: StateFlow<List<Archive>> = _list

    fun fetchPopularPrecious() {
        viewModelScope.launch {
            try {
                val response = api.getPopularPrecious()
                _preciousState.value = response
                if (response != null) {
                    Timber.d("获取每周必看成功")
                    _list.value = response.data.list

                    _list.value.forEach {
                        dao.insertArchive(it.toEntity())
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "获取每周必看失败")
                _preciousState.value = null
            }
        }
    }
}
