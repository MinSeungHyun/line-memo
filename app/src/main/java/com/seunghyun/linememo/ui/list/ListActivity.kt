package com.seunghyun.linememo.ui.list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
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
import kotlinx.android.synthetic.main.activity_list.*

const val CODE_CREATE_MEMO = 0
const val CODE_EDIT_MEMO = 1

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

        viewModel.startActivityForResult.observe(this, Observer { editingMemo ->
            val intent = Intent(this, EditActivity::class.java)
            if (editingMemo != null) {
                intent.putExtra(KEY_MEMO_ITEM, editingMemo)
                startActivityForResult(intent, CODE_EDIT_MEMO)
            } else {
                startActivityForResult(intent, CODE_CREATE_MEMO)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val memo = data?.getSerializableExtra(KEY_MEMO_ITEM) as? Memo
        if (resultCode != Activity.RESULT_OK || memo == null)
            return super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CODE_CREATE_MEMO -> viewModel.onMemoCreated(memo)
            CODE_EDIT_MEMO -> viewModel.onMemoEdited(memo)
        }
    }
}