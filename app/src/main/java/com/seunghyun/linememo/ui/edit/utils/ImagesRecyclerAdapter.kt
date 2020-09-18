package com.seunghyun.linememo.ui.edit.utils

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.seunghyun.linememo.R
import com.seunghyun.linememo.databinding.ItemImageBinding
import com.seunghyun.linememo.ui.edit.EditViewModel
import com.seunghyun.linememo.ui.edit.ImageItem

class ImagesRecyclerAdapter(private val viewModel: EditViewModel) :
    RecyclerView.Adapter<ImagesRecyclerAdapter.ImageViewHolder>() {

    private val items = arrayListOf<ImageItem>()

    fun updateItems(newItems: ArrayList<ImageItem>) {
        val callback = ImageItemDiffCallback(items, newItems)
        val result = DiffUtil.calculateDiff(callback)
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemImageBinding>(layoutInflater, R.layout.item_image, parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    inner class ImageViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageItem) {
            binding.apply {
                vm = viewModel
                this.item = item
            }
            loadImage(item)
        }

        private fun loadImage(item: ImageItem) = Glide.with(binding.root.context)
            .load(item.path)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, _1: Any?, _2: Target<Drawable>?, _3: Boolean): Boolean {
                    e?.printStackTrace()
                    viewModel.onImageLoadingError(item)
                    return true
                }

                override fun onResourceReady(
                    _1: Drawable?,
                    _2: Any?,
                    _3: Target<Drawable>?,
                    _4: DataSource?,
                    _5: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(binding.imageView)
    }
}
