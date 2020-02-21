package com.seunghyun.linememo.utils

import android.view.View
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import androidx.recyclerview.widget.RecyclerView
import com.seunghyun.linememo.data.Memo
import com.seunghyun.linememo.ui.edit.ImageItem
import com.seunghyun.linememo.ui.edit.utils.ImagesRecyclerAdapter
import com.seunghyun.linememo.ui.list.utils.MemoRecyclerAdapter

object BindingUtils {
    @BindingAdapter("app:items")
    @JvmStatic
    fun bindRecyclerViewImageItems(recyclerView: RecyclerView, items: ArrayList<ImageItem>) {
        val adapter = recyclerView.adapter as ImagesRecyclerAdapter
        adapter.updateItems(items)
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun bindRecyclerViewMemoItems(recyclerView: RecyclerView, items: ArrayList<Memo>) {
        val adapter = recyclerView.adapter as MemoRecyclerAdapter
        adapter.updateItems(items)
    }

    @BindingAdapter("app:calculateGuidePercent")
    @JvmStatic
    fun calculateGuidePercent(guideline: Guideline, memo: Memo) {
        val guidePercent = when {
            memo.title.isBlank() -> 0f
            memo.content.isBlank() -> 1f
            else -> 0.5f
        }
        guideline.setGuidelinePercent(guidePercent)
    }

    @BindingConversion
    @JvmStatic
    fun booleanToVisibility(isShowing: Boolean) = if (isShowing) View.VISIBLE else View.INVISIBLE
}