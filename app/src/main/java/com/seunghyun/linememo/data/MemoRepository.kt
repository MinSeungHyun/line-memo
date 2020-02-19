package com.seunghyun.linememo.data

class MemoRepository(private val memoDao: MemoDao) {
    fun instert(memo: Memo) = memoDao.insert(memo)
    fun getAllMemos() = memoDao.loadAll()
    fun update(memo: Memo) = memoDao.update(memo)
    fun delete(memo: Memo) = memoDao.delete(memo)
}