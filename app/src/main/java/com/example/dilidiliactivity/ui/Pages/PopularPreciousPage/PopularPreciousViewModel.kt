package com.example.dilidiliactivity.ui.Pages.PopularPreciousPage

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.dilidiliactivity.data.local.PopularPreciousResponse.PopularPreciousResponse
import com.example.dilidiliactivity.data.local.archive.ArchiveDao
import com.example.dilidiliactivity.data.local.archive.toEntity
import com.example.dilidiliactivity.data.remote.ApiClient.RetrofitClient
import com.example.dilidiliactivity.data.local.archive.Archive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.forEach

class PopularPreciousViewModel(
    private val dao: ArchiveDao
): ViewModel() {

    val TAG = "PopularPreciousVM"
    private val api = RetrofitClient.instance
    //获取PopularPrecious视频
    private var _preciousState = MutableStateFlow<PopularPreciousResponse?>(null)
    val preciousState: StateFlow<PopularPreciousResponse?> = _preciousState

    // 把 list 改成 StateFlow
    private val _list = MutableStateFlow<List<Archive>>(emptyList())
    val list: StateFlow<List<Archive>> = _list

    @OptIn(UnstableApi::class)
    fun fetchPopularPrecious() {
        viewModelScope.launch {
            try {
                val response = api.getPopularPrecious()
                _preciousState.value = response
                if (response != null) {
                    Log.d(TAG, "获取每周必看成功")
                    _list.value = response.data.list
                    Log.d(TAG, "_list: \n"+_list.value.toString())

                    _list.value.forEach {
                        dao.insertArchive(it.toEntity())
                        Log.d(TAG, "\n getVideoList读取到的archive ："+it.toEntity().toString()+"\n")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "获取每周必看失败: ${e.message}")
                _preciousState.value = null
            }
        }
    }
}