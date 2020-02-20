package com.seunghyun.linememo.utils

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seunghyun.linememo.data.Memo
import com.seunghyun.linememo.ui.edit.ImageItem
import com.seunghyun.linememo.ui.edit.utils.ImagesRecyclerAdapter
import com.seunghyun.linememo.ui.list.utils.MemoRecyclerAdapter

object BindingAdapters {
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

    @BindingAdapter("app:calculateDimensionRatio")
    @JvmStatic
    fun calculateDimensionRatio(imageView: ImageView, memo: Memo) {
        if (memo.images.isEmpty()) return
        val params = imageView.layoutParams as ConstraintLayout.LayoutParams
        params.dimensionRatio = "1:1"
        imageView.layoutParams = params
    }
}