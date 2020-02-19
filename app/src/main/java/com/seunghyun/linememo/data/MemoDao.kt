package com.seunghyun.linememo.data

import androidx.room.*

@Dao
interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memo: Memo)

    @Query("SELECT * FROM memo")
    suspend fun loadAll(): List<Memo>

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)
}
