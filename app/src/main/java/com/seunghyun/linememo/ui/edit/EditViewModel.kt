package com.seunghyun.linememo.ui.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditViewModel : ViewModel() {
    val eventTrigger = MutableLiveData<EditActivity.Event>()
    val imageItems = MutableLiveData(arrayListOf<ImageItem>())

    fun onAlbumButtonClick() {
        eventTrigger.value = EditActivity.Event.START_IMAGE_PICKER
    }

    fun onCameraButtonClick() {
        eventTrigger.value = EditActivity.Event.START_CAMERA
    }

    fun onLinkButtonClick() {
        eventTrigger.value = EditActivity.Event.ADD_LINK
    }
}