package com.seunghyun.linememo.ui.edit.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.ui.edit.EditViewModel

@Suppress("UNCHECKED_CAST")
class EditViewModelFactory(private val repository: MemoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = EditViewModel(repository) as T
}