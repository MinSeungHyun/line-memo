package com.seunghyun.linememo.ui.list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seunghyun.linememo.R
import com.seunghyun.linememo.data.AppDatabase
import com.seunghyun.linememo.data.MemoRepository
import com.seunghyun.linememo.databinding.ActivityListBinding
import com.seunghyun.linememo.ui.list.utils.ListViewModelFactory
import com.seunghyun.linememo.ui.list.utils.MemoRecyclerAdapter
import kotlinx.android.synthetic.main.activity_list.*

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
    }
}