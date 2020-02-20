package com.seunghyun.linememo.ui.list.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seunghyun.linememo.R
import com.seunghyun.linememo.data.Memo
import com.seunghyun.linememo.databinding.ItemMemoBinding
import com.seunghyun.linememo.ui.list.ListViewModel

class MemoRecyclerAdapter(private val viewModel: ListViewModel) : RecyclerView.Adapter<MemoRecyclerAdapter.MemoViewHolder>() {
    private val items = arrayListOf<Memo>()

    fun updateItems(newItems: ArrayList<Memo>) {
        val callback = MemoItemDiffCallback(items, newItems)
        val result = DiffUtil.calculateDiff(callback)
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemMemoBinding>(inflater, R.layout.item_memo, parent, false)
        return MemoViewHolder(binding)
    }

    override fun getItemCount() = viewModel.memos.value?.size ?: 0
    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) = holder.bind(viewModel.memos.value!![position])

    class MemoViewHolder(private val binding: ItemMemoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(memo: Memo) {
            binding.item = memo
            if (memo.images.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(memo.images[0].path)
                    .sizeMultiplier(0.5f)
                    .into(binding.thumbnailImage)
            }
        }
    }
}