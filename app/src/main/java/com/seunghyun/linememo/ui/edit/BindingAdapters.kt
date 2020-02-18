package com.seunghyun.linememo.ui.edit

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

object BindingAdapters {
    @BindingAdapter("app:items")
    @JvmStatic
    fun bindRecyclerViewItems(recyclerView: RecyclerView, items: ArrayList<ImageItem>) {
        val adapter = recyclerView.adapter as ImagesRecyclerAdapter
        adapter.items = items
        adapter.notifyDataSetChanged()
    }
}