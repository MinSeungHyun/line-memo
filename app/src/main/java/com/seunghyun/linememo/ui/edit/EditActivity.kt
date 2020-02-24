package com.seunghyun.linememo.ui.edit

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
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
import com.seunghyun.linememo.data.Memo
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.databinding.ActivityEditBinding
import com.seunghyun.linememo.databinding.PopupAddNewImageBinding
import com.seunghyun.linememo.ui.edit.utils.EditViewModelFactory
import com.seunghyun.linememo.ui.edit.utils.ImagesRecyclerAdapter
import com.seunghyun.linememo.ui.list.KEY_MEMO_ITEM
import com.seunghyun.linememo.ui.list.RESULT_DELETED
import com.seunghyun.linememo.utils.copyImageToStorage
import com.seunghyun.linememo.utils.getImagePathForCurrent
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.dialog_add_link.*
import java.io.File

private const val REQUEST_IMAGE_PICKER = 0
private const val REQUEST_CAMERA = 1

class EditActivity : AppCompatActivity() {
    private val viewModel by lazy {
        val dao = AppDatabase.getInstance(this).memoDao()
        val repository = MemoRepository(dao)
        val inputMemo = intent.getSerializableExtra(KEY_MEMO_ITEM) as Memo?
        ViewModelProvider(this, EditViewModelFactory(repository, inputMemo)).get(EditViewModel::class.java)
    }
    private lateinit var imageUri: Uri
    private var addImagePopup: PopupWindow? = null
    private var menu: Menu? = null

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
        this.menu = menu
        updateMenuItem()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.saveButton -> {
            viewModel.onSaveButtonClick()
            true
        }
        R.id.editButton -> {
            viewModel.onEditButtonClick()
            true
        }
        R.id.deleteButton -> {
            viewModel.onDeleteButtonClick()
            true
        }
        android.R.id.home -> {
            finish()
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

        contentText.movementMethod = ScrollingMovementMethod()

        viewModel.eventTrigger.observe(this, Observer {
            when (it!!) {
                Event.StartImagePicker -> startImagePicker()
                Event.StartCamera -> startCamera()
                Event.AddLink -> startAddLinkDialog()
                Event.ImageLoadingError -> showToast(R.string.image_loading_failed)
                Event.InvalidMemo -> showToast(R.string.enter_title)
                Event.Finish -> finish()
                is Event.MemoSaved -> onMemoSaved((it as Event.MemoSaved).memo)
                is Event.MemoDeleted -> onMemoDeleted((it as Event.MemoDeleted).memo)
                is Event.DeleteLocalImageFile -> deleteLocalImageFile((it as Event.DeleteLocalImageFile).images)
            }
        })
        viewModel.isEditing.observe(this, Observer {
            updateMenuItem()
            //item_image.xml 에는 lifecycleOwner 가 없기 때문에 이미지 삭제 버튼 업데이트를 위해 다시 binding 해야한다.
            imagesRecyclerView.adapter?.notifyDataSetChanged()
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
            viewModel.addNewImage(editText.text.toString())
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
                viewModel.addNewImage(copiedUri.path!!)
            }
            REQUEST_CAMERA -> {
                viewModel.addNewImage(imageUri.toString())
            }
            else -> return super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun updateMenuItem() {
        menu ?: return
        val isEditing = viewModel.isEditing.value!!
        val editButton = menu!!.findItem(R.id.editButton)
        val saveButton = menu!!.findItem(R.id.saveButton)
        if (isEditing) {
            editButton.isVisible = false
            saveButton.isVisible = true
        } else {
            editButton.isVisible = true
            saveButton.isVisible = false
        }
    }

    private fun onMemoSaved(memo: Memo) {
        val intent = Intent().apply { putExtra(KEY_MEMO_ITEM, memo) }
        setResult(Activity.RESULT_OK, intent)
        showToast(R.string.saved)
        finish()
    }

    private fun onMemoDeleted(memo: Memo) {
        val intent = Intent().apply { putExtra(KEY_MEMO_ITEM, memo) }
        setResult(RESULT_DELETED, intent)
        showToast(R.string.deleted)
        finish()
    }

    private fun deleteLocalImageFile(images: List<ImageItem>) {
        for (item in images) {
            if (item.path.startsWith("http")) continue
            val fileName = item.path.split("/").last()
            val file = File("${filesDir.path}/$fileName")
            if (file.exists()) file.delete()
        }
    }

    private fun showToast(@StringRes stringId: Int) {
        Toast.makeText(this, stringId, Toast.LENGTH_LONG).show()
    }

    override fun onPause() {
        super.onPause()
        addImagePopup?.dismiss()
    }

    override fun finish() {
        if (!viewModel.hasChanges) return super.finish()
        AlertDialog.Builder(this)
            .setTitle(R.string.save_question)
            .setPositiveButton(R.string.save) { _, _ ->
                val canSave = viewModel.onSaveButtonClick()
                if (canSave) super.finish()
            }
            .setNegativeButton(R.string.dont_save) { _, _ ->
                super.finish()
            }
            .setNeutralButton(R.string.cancel, null)
            .show()
    }

    sealed class Event {
        object StartImagePicker : Event()
        object StartCamera : Event()
        object AddLink : Event()
        object ImageLoadingError : Event()
        object InvalidMemo : Event()
        object Finish : Event()
        class MemoSaved(val memo: Memo) : Event()
        class MemoDeleted(val memo: Memo) : Event()
        class DeleteLocalImageFile(val images: List<ImageItem>) : Event()
    }
}