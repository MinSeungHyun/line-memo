package com.seunghyun.linememo.ui.edit.utils

import androidx.recyclerview.widget.DiffUtil
import com.seunghyun.linememo.ui.edit.ImageItem

class ImageItemDiffCallback(private val oldList: ArrayList<ImageItem>, private val newList: ArrayList<ImageItem>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].path == newList[newItemPosition].path
    }
}