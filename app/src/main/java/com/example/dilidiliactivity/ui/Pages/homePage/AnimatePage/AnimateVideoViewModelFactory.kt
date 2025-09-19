package com.example.dilidiliactivity.ui.Pages.homePage.AnimatePage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dilidiliactivity.domain.repository.VideoRepository

//使用Factory避免了在ViewModel中直接new对象
class AnimateVideoViewModelFactory(
    private val repo: VideoRepository       //传入仓库对象、供viewModel使用
) : ViewModelProvider.Factory {         //实现了ViewModelProvider.Factory接口
    override fun <T : ViewModel> create(modelClass: Class<T>): T {      //实现create方法，modelClass表示想要的ViewModel类型
        if (modelClass.isAssignableFrom(AnimateVideoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")                     //消除泛型强转的警告
            return AnimateVideoViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
