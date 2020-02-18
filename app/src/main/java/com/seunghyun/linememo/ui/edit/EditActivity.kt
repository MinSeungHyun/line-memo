package com.seunghyun.linememo.ui.edit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seunghyun.linememo.R
import com.seunghyun.linememo.databinding.ActivityEditBinding
import com.seunghyun.linememo.ui.edit.utils.ImagesRecyclerAdapter
import com.seunghyun.linememo.utils.addItem
import java.io.File
import java.io.FileOutputStream

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
        val uri = data?.data
        if (resultCode != Activity.RESULT_OK || uri == null)
            return super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            IMAGE_PICKER_REQUEST_CODE -> {
                val copiedUri = copyImageToStorage(uri)
                viewModel.imageItems.addItem(ImageItem(copiedUri.path!!))
            }
            else -> return super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun copyImageToStorage(uri: Uri): Uri {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
        return saveBitmapToStorage(bitmap)
    }

    private fun saveBitmapToStorage(bitmap: Bitmap): Uri {
        val path = File(filesDir, "${System.currentTimeMillis()}.png")
        val fos = FileOutputStream(path)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        return Uri.parse(path.path)
    }

    enum class Event {
        START_IMAGE_PICKER,
        START_CAMERA,
        ADD_LINK
    }
}