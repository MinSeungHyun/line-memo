package com.seunghyun.linememo.ui.list.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.ui.list.ListViewModel

@Suppress("UNCHECKED_CAST")
class ListViewModelFactory(private val repository: MemoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = ListViewModel(repository) as T
}