package com.seunghyun.linememo.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seunghyun.linememo.R
import com.seunghyun.linememo.data.AppDatabase
import com.seunghyun.linememo.data.Memo
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.databinding.ActivityListBinding
import com.seunghyun.linememo.ui.edit.EditActivity
import com.seunghyun.linememo.ui.list.utils.ListViewModelFactory
import com.seunghyun.linememo.ui.list.utils.MemoRecyclerAdapter
import com.seunghyun.linememo.utils.EventObserver
import kotlinx.android.synthetic.main.activity_list.*

const val REQUEST_CREATE_MEMO = 0
const val REQUEST_EDIT_MEMO = 1
const val RESULT_DELETED = 5

class ListActivity : AppCompatActivity() {
    private val viewModel by lazy {
        val dao = AppDatabase.getInstance(this).memoDao()
        val repository = MemoRepository(dao)
        ViewModelProvider(this, ListViewModelFactory(repository)).get(ListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityListBinding>(this, R.layout.activity_list).apply {
            vm = viewModel
            lifecycleOwner = this@ListActivity
        }
        initView()
    }

    private fun initView() {
        memoRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            adapter = MemoRecyclerAdapter(viewModel)
        }

        viewModel.startActivityForResult.observe(this, EventObserver { editingMemo ->
            val intent = Intent(this, EditActivity::class.java)
            if (editingMemo != null) {
                intent.putExtra(KEY_MEMO_ITEM, editingMemo)
                startActivityForResult(intent, REQUEST_EDIT_MEMO)
            } else {
                startActivityForResult(intent, REQUEST_CREATE_MEMO)
            }
        })

        viewModel.scrollToTop.observe(this, EventObserver {
            memoRecyclerView.smoothScrollToPosition(0)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val memo = data?.getSerializableExtra(KEY_MEMO_ITEM) as? Memo
            ?: return super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_DELETED) {
            viewModel.onMemoDeleted(memo)
        } else if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CREATE_MEMO -> viewModel.onMemoCreated(memo)
                REQUEST_EDIT_MEMO -> viewModel.onMemoEdited(memo)
            }
        }
    }
}