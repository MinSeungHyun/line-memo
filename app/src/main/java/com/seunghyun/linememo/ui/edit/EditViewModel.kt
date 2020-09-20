package com.seunghyun.linememo.ui.edit

import androidx.lifecycle.*
import com.seunghyun.linememo.data.Memo
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.utils.Event
import com.seunghyun.linememo.utils.addItem
import com.seunghyun.linememo.utils.removeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditViewModel(private val repository: MemoRepository, private val inputMemo: Memo? = null) : ViewModel() {
    private val _eventTrigger = MutableLiveData<Event<EditActivity.Event>>()
    private val _isEditing = MutableLiveData(false)
    private val _imageItems = MutableLiveData(arrayListOf<ImageItem>())

    val eventTrigger: LiveData<Event<EditActivity.Event>> = _eventTrigger
    val isEditing: LiveData<Boolean> = _isEditing
    val imageItems: LiveData<ArrayList<ImageItem>> = _imageItems

    val title = MutableLiveData("")
    val content = MutableLiveData("")

    var hasChanges = false

    private val titleChangeDetectObserver = ChangeDetectObserver<String>()
    private val contentChangeDetectObserver = ChangeDetectObserver<String>()
    private val imageItemsChangeDetectObserver = ChangeDetectObserver<ArrayList<ImageItem>>()

    init {
        if (inputMemo == null) _isEditing.value = true
        else {
            title.value = inputMemo.title
            content.value = inputMemo.content
            _imageItems.value = ArrayList(inputMemo.images)
        }

        startObservingChanges()
    }

    fun onAlbumButtonClick() {
        _eventTrigger.value = Event(EditActivity.Event.StartImagePicker)
    }

    fun onCameraButtonClick() {
        _eventTrigger.value = Event(EditActivity.Event.StartCamera)
    }

    fun onLinkButtonClick() {
        _eventTrigger.value = Event(EditActivity.Event.AddLink)
    }

    fun addNewImage(path: String) {
        _imageItems.addItem(ImageItem(path))
    }

    /**
     * @return true if the memo can be saved
     */
    fun onSaveButtonClick(): Boolean {
        val memo = createMemoItem()
        if (memo.isNotValid()) {
            _eventTrigger.value = Event(EditActivity.Event.InvalidMemo)
            return false
        }
        saveOrUpdateMemo(memo)
        return true
    }

    fun onEditButtonClick() {
        _isEditing.value = true
    }

    fun onDeleteButtonClick() {
        if (inputMemo == null) _eventTrigger.value = Event(EditActivity.Event.Finish)
        else viewModelScope.launch {
            launch(Dispatchers.IO) {
                repository.delete(inputMemo)
                _eventTrigger.postValue(Event(EditActivity.Event.DeleteLocalImageFile(inputMemo.images)))
            }
            _eventTrigger.value = Event(EditActivity.Event.MemoDeleted(inputMemo))
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
                if (inputMemo == null) repository.insert(memo)
                else repository.update(memo)
            }
            hasChanges = false
            _eventTrigger.value = Event(EditActivity.Event.MemoSaved(memo))
        }
    }

    fun onImageLoadingError(item: ImageItem) {
        if (inputMemo == null) _imageItems.removeItem(item)
        _eventTrigger.value = Event(EditActivity.Event.ImageLoadingError)
    }

    fun onDeleteImageButtonClick(item: ImageItem) {
        _imageItems.removeItem(item)
        _eventTrigger.value = Event(EditActivity.Event.DeleteLocalImageFile(listOf(item)))
    }

    private fun startObservingChanges() {
        title.observeForever(titleChangeDetectObserver)
        content.observeForever(contentChangeDetectObserver)
        imageItems.observeForever(imageItemsChangeDetectObserver)
    }

    override fun onCleared() {
        title.removeObserver(titleChangeDetectObserver)
        content.removeObserver(contentChangeDetectObserver)
        imageItems.removeObserver(imageItemsChangeDetectObserver)
    }

    inner class ChangeDetectObserver<T> : Observer<T> {
        private var isFirstObserve = true

        override fun onChanged(t: T) {
            if (isFirstObserve) {
                isFirstObserve = false
                return
            }
            if (isEditing.value == true) hasChanges = true
        }
    }
}
