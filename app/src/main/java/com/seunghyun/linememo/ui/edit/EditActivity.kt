package com.seunghyun.linememo.ui.edit

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seunghyun.linememo.R
import com.seunghyun.linememo.databinding.ActivityEditBinding
import com.seunghyun.linememo.databinding.PopupAddNewImageBinding
import com.seunghyun.linememo.ui.edit.utils.ImagesRecyclerAdapter
import com.seunghyun.linememo.utils.addItem
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.dialog_add_link.*
import java.io.File
import java.io.FileOutputStream

private const val REQUEST_IMAGE_PICKER = 0
private const val REQUEST_CAMERA = 1

class EditActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(EditViewModel::class.java) }
    private lateinit var imageUri: Uri
    private lateinit var addImagePopup: PopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.write_memo)
        DataBindingUtil.setContentView<ActivityEditBinding>(this, R.layout.activity_edit).apply {
            vm = viewModel
            lifecycleOwner = this@EditActivity
        }

        imagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = ImagesRecyclerAdapter(viewModel)
        }

        addImageButton.setOnClickListener {
            showAddImagePopup()
        }

        viewModel.eventTrigger.observe(this, Observer {
            when (it!!) {
                Event.START_IMAGE_PICKER -> startImagePicker()
                Event.START_CAMERA -> startCamera()
                Event.ADD_LINK -> startAddLinkDialog()
                Event.IMAGE_LOADING_ERROR -> showLoadingErrorToast()
            }
        })
    }

    private fun showAddImagePopup() {
        val binding = DataBindingUtil.inflate<PopupAddNewImageBinding>(layoutInflater,
            R.layout.popup_add_new_image, rootView, false)
        binding.vm = viewModel
        addImagePopup = PopupWindow(binding.root, addImageButton.width, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            elevation = 8f
            isOutsideTouchable = true
            showAsDropDown(addImageButton)
        }
    }

    private fun startImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }

    private fun startCamera() = Intent(MediaStore.ACTION_IMAGE_CAPTURE).run {
        resolveActivity(packageManager)
        val file = getImageFileForCurrent()
        imageUri = FileProvider.getUriForFile(this@EditActivity, "${packageName}.provider", file)
        putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(this, REQUEST_CAMERA)
    }

    private fun startAddLinkDialog() = AlertDialog.Builder(this)
        .setView(R.layout.dialog_add_link)
        .setIcon(R.drawable.ic_link_black_24dp)
        .setTitle(R.string.link)
        .setMessage(R.string.type_link)
        .setNegativeButton(R.string.cancel, null)
        .setPositiveButton(R.string.ok) { dialog, _ ->
            val editText = (dialog as Dialog).linkEditText
            viewModel.imageItems.addItem(ImageItem(editText.text.toString()))
        }
        .show()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_IMAGE_PICKER -> {
                val uri = data?.data ?: run {
                    showLoadingErrorToast()
                    return super.onActivityResult(requestCode, resultCode, data)
                }
                val copiedUri = copyImageToStorage(uri)
                viewModel.imageItems.addItem(ImageItem(copiedUri.path!!))
            }
            REQUEST_CAMERA -> {
                viewModel.imageItems.addItem(ImageItem(imageUri.toString()))
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
        val path = getImageFileForCurrent()
        val fos = FileOutputStream(path)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        return Uri.parse(path.path)
    }

    private fun getImageFileForCurrent() = File(filesDir, "${System.currentTimeMillis()}.jpg")

    private fun showLoadingErrorToast() {
        Toast.makeText(this, R.string.image_loading_failed, Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        super.onPause()
        addImagePopup.dismiss()
    }

    enum class Event {
        START_IMAGE_PICKER,
        START_CAMERA,
        ADD_LINK,
        IMAGE_LOADING_ERROR
    }
}