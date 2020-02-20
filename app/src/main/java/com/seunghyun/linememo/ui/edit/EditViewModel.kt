package com.seunghyun.linememo.ui.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seunghyun.linememo.data.Memo
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.utils.removeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditViewModel(private val repository: MemoRepository, private val inputMemo: Memo? = null) : ViewModel() {
    val eventTrigger = MutableLiveData<EditActivity.Event>()
    val isEditing = MutableLiveData(true)
    val title = MutableLiveData("")
    val content = MutableLiveData("")
    val imageItems = MutableLiveData(arrayListOf<ImageItem>())
    val isFirstEdit = inputMemo != null

    fun onAlbumButtonClick() {
        eventTrigger.value = EditActivity.Event.START_IMAGE_PICKER
    }

    fun onCameraButtonClick() {
        eventTrigger.value = EditActivity.Event.START_CAMERA
    }

    fun onLinkButtonClick() {
        eventTrigger.value = EditActivity.Event.ADD_LINK
    }

    fun onSaveButtonClick() {
        val memo = Memo(
            System.currentTimeMillis(),
            title.value?.trim() ?: "",
            content.value?.trim() ?: "",
            imageItems.value ?: listOf()
        )
        if (memo.isNotValid()) {
            eventTrigger.value = EditActivity.Event.NOTHING_TO_SAVE
            return
        }
        viewModelScope.launch {
            launch(Dispatchers.IO) { repository.insert(memo) }
            eventTrigger.value = EditActivity.Event.MEMO_SAVED
        }
    }

    fun onImageLoadingError(item: ImageItem) {
        imageItems.removeItem(item)
        eventTrigger.value = EditActivity.Event.IMAGE_LOADING_ERROR
    }
}