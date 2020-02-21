package com.seunghyun.linememo.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seunghyun.linememo.data.Memo
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.utils.SingleLiveEvent
import com.seunghyun.linememo.utils.removeItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val KEY_MEMO_ITEM = "memoItem"

class ListViewModel(private val repository: MemoRepository) : ViewModel() {
    val startActivityForResult = SingleLiveEvent<Memo?>()
    val scrollToTop = SingleLiveEvent<Void>()
    val memos = MutableLiveData(arrayListOf<Memo>())
    val refreshing = MutableLiveData(false)
    private var newMemoInserted = false

    init {
        updateAllMemos()
    }

    fun updateAllMemos() = viewModelScope.launch(Dispatchers.Main) {
        refreshing.postValue(true)
        val memoList = ArrayList(repository.getAllMemos().reversed())
        memos.postValue(memoList)
        refreshing.postValue(false)
    }

    fun onAddButtonClick() {
        startActivityForResult.value = null
    }

    fun onMemoItemClick(memo: Memo) {
        startActivityForResult.value = memo
    }

    fun onMemoCreated(memo: Memo) {
        newMemoInserted = true
        val memos = memos.value ?: return
        memos.add(0, memo)
        this.memos.value = memos
    }

    fun onMemoEdited(newMemo: Memo) {
        val memos = memos.value ?: return
        val oldMemo = memos.find { it.createdMillis == newMemo.createdMillis } ?: return
        val oldMemoIndex = memos.indexOf(oldMemo)
        memos.removeAt(oldMemoIndex)
        memos.add(oldMemoIndex, newMemo)
        this.memos.value = memos
    }

    fun onMemoDeleted(deletedMemo: Memo) {
        memos.removeItem(deletedMemo)
    }

    fun onRecyclerViewUpdated() {
        if (newMemoInserted) {
            scrollToTop.call()
            newMemoInserted = false
        }
    }
}