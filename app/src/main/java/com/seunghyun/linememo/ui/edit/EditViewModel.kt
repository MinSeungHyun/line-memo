package com.seunghyun.linememo.ui.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditViewModel : ViewModel() {
    val eventTrigger = MutableLiveData<EditActivity.Event>()
    val imageItems = MutableLiveData(arrayListOf<ImageItem>())

    fun onAddNewImageButtonClick() {
        eventTrigger.value = EditActivity.Event.START_IMAGE_PICKER
    }
}