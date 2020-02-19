package com.seunghyun.linememo.data

import androidx.room.*

@Dao
interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(memo: Memo)

    @Query("SELECT * FROM memo")
    fun loadAll(): List<Memo>

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun update(memo: Memo)

    @Delete
    fun delete(memo: Memo)
}
