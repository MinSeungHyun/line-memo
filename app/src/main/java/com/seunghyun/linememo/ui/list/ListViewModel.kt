package com.seunghyun.linememo.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seunghyun.linememo.data.Memo
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val KEY_MEMO_ITEM = "memoItem"

class ListViewModel(private val repository: MemoRepository) : ViewModel() {
    val startActivityForResult = SingleLiveEvent<Memo?>()
    val memos = MutableLiveData(arrayListOf<Memo>())

    init {
        updateAllMemos()
    }

    private fun updateAllMemos() = viewModelScope.launch(Dispatchers.Main) {
        val memoList = ArrayList(repository.getAllMemos().reversed())
        memos.postValue(memoList)
    }

    fun onAddButtonClick() {
        startActivityForResult.value = null
    }

    fun onMemoItemClick(memo: Memo) {
        startActivityForResult.value = memo
    }

    fun onMemoCreated(memo: Memo) {
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
}