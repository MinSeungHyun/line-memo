package com.seunghyun.linememo.ui.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seunghyun.linememo.R
import com.seunghyun.linememo.databinding.ActivityEditBinding
import com.seunghyun.linememo.utils.addItem

private const val IMAGE_PICKER_REQUEST_CODE = 77

class EditActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(EditViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.write_memo)
        DataBindingUtil.setContentView<ActivityEditBinding>(this, R.layout.activity_edit).apply {
            vm = viewModel
            lifecycleOwner = this@EditActivity

            imagesRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@EditActivity, LinearLayoutManager.HORIZONTAL, false)
                adapter = ImagesRecyclerAdapter(viewModel)
            }
        }

        viewModel.eventTrigger.observe(this, Observer {
            when (it!!) {
                Event.START_IMAGE_PICKER -> startImagePicker()
            }
        })
    }

    private fun startImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val path = data?.data?.toString()
        if (resultCode != Activity.RESULT_OK || path == null)
            return super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            IMAGE_PICKER_REQUEST_CODE -> {
                viewModel.imageItems.addItem(ImageItem(path))
            }
            else -> return super.onActivityResult(requestCode, resultCode, data)
        }
    }

    enum class Event {
        START_IMAGE_PICKER
    }
}