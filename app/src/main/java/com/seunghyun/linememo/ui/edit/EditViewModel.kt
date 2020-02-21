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
    val isEditing = MutableLiveData(false)
    val title = MutableLiveData("")
    val content = MutableLiveData("")
    val imageItems = MutableLiveData(arrayListOf<ImageItem>())
    private val isFirstEdit = inputMemo == null

    init {
        if (isFirstEdit) isEditing.value = true
        else {
            title.value = inputMemo!!.title
            content.value = inputMemo.content
            imageItems.value = ArrayList(inputMemo.images)
        }
    }

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
        val memo = createMemoItem()
        if (memo.isNotValid()) {
            eventTrigger.value = EditActivity.Event.NOTHING_TO_SAVE
            return
        }
        saveOrUpdateMemo(memo)
    }

    fun onEditButtonClick() {
        isEditing.value = true
    }

    private fun createMemoItem(): Memo {
        val currentTimeMillis = System.currentTimeMillis()
        return Memo(
            inputMemo?.createdMillis ?: currentTimeMillis,
            currentTimeMillis,
            title.value?.trim() ?: "",
            content.value?.trim() ?: "",
            imageItems.value ?: listOf()
        )
    }

    private fun saveOrUpdateMemo(memo: Memo) {
        viewModelScope.launch {
            launch(Dispatchers.IO) {
                if (isFirstEdit) repository.insert(memo)
                else repository.update(memo)
            }
            eventTrigger.value = EditActivity.Event.MEMO_SAVED
        }
    }

    fun onImageLoadingError(item: ImageItem) {
        imageItems.removeItem(item)
        eventTrigger.value = EditActivity.Event.IMAGE_LOADING_ERROR
    }
}