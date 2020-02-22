package com.seunghyun.linememo.ui.list.utils

import androidx.recyclerview.widget.DiffUtil
import com.seunghyun.linememo.data.Memo

class MemoItemDiffCallback(private val oldList: ArrayList<Memo>, private val newList: ArrayList<Memo>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].contentEquals(newList[newItemPosition])
    }
}