package com.seunghyun.linememo.ui.edit.utils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seunghyun.linememo.ui.edit.ImageItem

object BindingAdapters {
    @BindingAdapter("app:items")
    @JvmStatic
    fun bindRecyclerViewItems(recyclerView: RecyclerView, items: ArrayList<ImageItem>) {
        val adapter = recyclerView.adapter as ImagesRecyclerAdapter
        adapter.updateItems(items)
    }
}