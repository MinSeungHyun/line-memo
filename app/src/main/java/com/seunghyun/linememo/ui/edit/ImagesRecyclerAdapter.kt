package com.seunghyun.linememo.ui.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seunghyun.linememo.R
import com.seunghyun.linememo.databinding.ItemImageBinding
import com.seunghyun.linememo.databinding.ItemNewImageBinding

private const val TYPE_IMAGE = 0
private const val TYPE_NEW_IMAGE_BUTTON = 1

class ImagesRecyclerAdapter(private val viewModel: EditViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items = arrayListOf<ImageItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return if (viewType == TYPE_IMAGE) {
            val binding = DataBindingUtil.inflate<ItemImageBinding>(layoutInflater, R.layout.item_image, parent, false)
            ImageViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemNewImageBinding>(layoutInflater, R.layout.item_new_image, parent, false)
            NewImageButtonViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = when (holder) {
        is ImageViewHolder -> holder.bind(items[position])
        is NewImageButtonViewHolder -> holder.bind()
        else -> throw TypeCastException("Cannot find ViewHolder")
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size) TYPE_NEW_IMAGE_BUTTON
        else TYPE_IMAGE
    }

    override fun getItemCount() = items.size + 1

    class ImageViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageItem) {
            Glide.with(binding.root.context)
                    .load(item.path)
                    .into(binding.imageView)
        }
    }

    inner class NewImageButtonViewHolder(private val binding: ItemNewImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.vm = viewModel
        }
    }
}