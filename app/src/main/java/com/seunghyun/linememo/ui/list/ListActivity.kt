package com.seunghyun.linememo.ui.list

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.seunghyun.linememo.R
import com.seunghyun.linememo.databinding.ActivityListBinding
import com.seunghyun.linememo.ui.new_memo.NewMemoActivity

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityListBinding>(this, R.layout.activity_list)

        binding.addButton.setOnClickListener {
            startActivity(Intent(this, NewMemoActivity::class.java))
        }
    }
}