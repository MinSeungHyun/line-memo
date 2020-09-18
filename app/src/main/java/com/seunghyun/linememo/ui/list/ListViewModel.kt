package com.seunghyun.linememo.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seunghyun.linememo.data.Memo
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.utils.Event
import com.seunghyun.linememo.utils.removeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val KEY_MEMO_ITEM = "memoItem"

class ListViewModel(private val repository: MemoRepository) : ViewModel() {
    private val _startActivityForResult = MutableLiveData<Event<Memo?>>()
    private val _scrollToTop = MutableLiveData<Event<Void?>>()
    private val _memos = MutableLiveData(arrayListOf<Memo>())
    private val _refreshing = MutableLiveData(false)

    val startActivityForResult: LiveData<Event<Memo?>> = _startActivityForResult
    val scrollToTop: LiveData<Event<Void?>> = _scrollToTop
    val memos: LiveData<ArrayList<Memo>> = _memos
    val refreshing: LiveData<Boolean> = _refreshing

    private var newMemoInserted = false

    init {
        updateAllMemos()
    }

    fun updateAllMemos() = viewModelScope.launch(Dispatchers.Main) {
        _refreshing.postValue(true)
        val memoList = ArrayList(repository.getAllMemos().reversed())
        _memos.postValue(memoList)
        _refreshing.postValue(false)
    }

    fun onAddButtonClick() {
        _startActivityForResult.value = Event(null)
    }

    fun onMemoItemClick(memo: Memo) {
        _startActivityForResult.value = Event(memo)
    }

    fun onMemoCreated(memo: Memo) {
        newMemoInserted = true
        val memos = memos.value ?: return
        memos.add(0, memo)
        _memos.value = memos
    }

    fun onMemoEdited(newMemo: Memo) {
        val memos = memos.value ?: return
        val oldMemo = memos.find { it.createdMillis == newMemo.createdMillis } ?: return
        val oldMemoIndex = memos.indexOf(oldMemo)
        memos.removeAt(oldMemoIndex)
        memos.add(oldMemoIndex, newMemo)
        _memos.value = memos
    }

    fun onMemoDeleted(deletedMemo: Memo) {
        _memos.removeItem(deletedMemo)
    }

    fun onRecyclerViewUpdated() {
        if (newMemoInserted) {
            _scrollToTop.value = Event(null)
            newMemoInserted = false
        }
    }
}
