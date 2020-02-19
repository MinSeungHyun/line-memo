package com.seunghyun.linememo.ui.edit

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seunghyun.linememo.R
import com.seunghyun.linememo.data.AppDatabase
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.databinding.ActivityEditBinding
import com.seunghyun.linememo.databinding.PopupAddNewImageBinding
import com.seunghyun.linememo.ui.edit.utils.EditViewModelFactory
import com.seunghyun.linememo.ui.edit.utils.ImagesRecyclerAdapter
import com.seunghyun.linememo.utils.addItem
import com.seunghyun.linememo.utils.copyImageToStorage
import com.seunghyun.linememo.utils.getImagePathForCurrent
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.dialog_add_link.*

private const val REQUEST_IMAGE_PICKER = 0
private const val REQUEST_CAMERA = 1

class EditActivity : AppCompatActivity() {
    private val viewModel by lazy {
        val dao = AppDatabase.getInstance(this).memoDao()
        val repository = MemoRepository(dao)
        ViewModelProvider(this, EditViewModelFactory(repository)).get(EditViewModel::class.java)
    }
    private lateinit var imageUri: Uri
    private var addImagePopup: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.write_memo)
        DataBindingUtil.setContentView<ActivityEditBinding>(this, R.layout.activity_edit).apply {
            vm = viewModel
            lifecycleOwner = this@EditActivity
        }
        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.saveButton -> {
            viewModel.onSaveButtonClick()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun initView() {
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
                Event.MEMO_SAVED -> {
                    showToast(R.string.saved)
                    finish()
                }
                Event.IMAGE_LOADING_ERROR -> showToast(R.string.image_loading_failed)
                Event.NOTHING_TO_SAVE -> showToast(R.string.nothing_to_save)
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
        val file = getImagePathForCurrent()
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
                    showToast(R.string.image_loading_failed)
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

    private fun showToast(@StringRes stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        super.onPause()
        addImagePopup?.dismiss()
    }

    enum class Event {
        START_IMAGE_PICKER,
        START_CAMERA,
        ADD_LINK,
        IMAGE_LOADING_ERROR,
        NOTHING_TO_SAVE,
        MEMO_SAVED
    }
}