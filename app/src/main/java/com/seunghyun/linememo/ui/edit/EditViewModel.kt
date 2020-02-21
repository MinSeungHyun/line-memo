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
    val isFirstEdit = inputMemo == null

    init {
        if (isFirstEdit) isEditing.value = true
        else {
            title.value = inputMemo!!.title
            content.value = inputMemo.content
            imageItems.value = ArrayList(inputMemo.images)
        }
    }

    fun onAlbumButtonClick() {
        eventTrigger.value = EditActivity.Event.StartImagePicker
    }

    fun onCameraButtonClick() {
        eventTrigger.value = EditActivity.Event.StartCamera
    }

    fun onLinkButtonClick() {
        eventTrigger.value = EditActivity.Event.AddLink
    }

    fun onSaveButtonClick() {
        val memo = createMemoItem()
        if (memo.isNotValid()) {
            eventTrigger.value = EditActivity.Event.InvalidMemo
            return
        }
        saveOrUpdateMemo(memo)
    }

    fun onEditButtonClick() {
        isEditing.value = true
    }

    fun onDeleteButtonClick() {
        if (isFirstEdit) eventTrigger.value = EditActivity.Event.Finish
        else viewModelScope.launch {
            launch(Dispatchers.IO) { repository.delete(inputMemo!!) }
            eventTrigger.value = EditActivity.Event.MemoDeleted(inputMemo!!)
        }
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
            eventTrigger.value = EditActivity.Event.MemoSaved(memo)
        }
    }

    fun onImageLoadingError(item: ImageItem) {
        if (isFirstEdit) imageItems.removeItem(item)
        eventTrigger.value = EditActivity.Event.ImageLoadingError
    }

    fun onDeleteImageButtonClick(item: ImageItem) {
        imageItems.removeItem(item)
    }
}