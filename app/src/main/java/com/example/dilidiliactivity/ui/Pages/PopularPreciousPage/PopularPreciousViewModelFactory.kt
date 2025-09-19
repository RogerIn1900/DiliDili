package com.example.dilidiliactivity.ui.Pages.PopularPreciousPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dilidiliactivity.data.local.archive.ArchiveDao
import com.example.dilidiliactivity.domain.repository.VideoRepository

class PopularPreciousViewModelFactory(
    private val dao: ArchiveDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PopularPreciousViewModel::class.java)) {
            return PopularPreciousViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}