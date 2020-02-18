package com.seunghyun.linememo.utils

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<ArrayList<T>>.addItem(item: T) {
    val list = this.value ?: arrayListOf()
    list.add(item)
    this.value = list
}