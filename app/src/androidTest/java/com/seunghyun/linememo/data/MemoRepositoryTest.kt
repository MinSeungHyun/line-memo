package com.seunghyun.linememo.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.seunghyun.linememo.ui.edit.ImageItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MemoRepositoryTest {
    private val testMemo1 = Memo(
        System.currentTimeMillis(),
        System.currentTimeMillis(),
        "Title",
        "content",
        listOf(ImageItem("https://test.com"))
    )
    private val testMemo2 = Memo(
        System.currentTimeMillis() + 1,
        System.currentTimeMillis() + 1,
        "Title2",
        "content2",
        listOf(ImageItem("https://test2.com"))
    )

    private lateinit var db: AppDatabase
    private lateinit var repository: MemoRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repository = MemoRepository(db.memoDao())
    }

    @Test
    fun insertMemo() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(testMemo1)
            repository.insert(testMemo2)
            val memos = repository.getAllMemos()
            launch(Dispatchers.Main) {
                assertTrue(memos[0].contentEquals(testMemo1))
                assertTrue(memos[1].contentEquals(testMemo2))
            }
        }
    }

    @Test
    fun updateMemo() {
        val testMemo1Updated = Memo(
            testMemo1.createdMillis,
            System.currentTimeMillis(),
            "Title updated",
            "content updated",
            listOf()
        )
        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(testMemo1)
            repository.insert(testMemo2)
            repository.update(testMemo1Updated)
            val memos = repository.getAllMemos()
            launch(Dispatchers.Main) {
                assertTrue(memos[0].contentEquals(testMemo1Updated))
                assertTrue(memos[1].contentEquals(testMemo2))
            }
        }
    }

    @Test
    fun deleteMemo() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insert(testMemo1)
            repository.insert(testMemo2)
            repository.delete(testMemo1)
            repository.delete(testMemo2)
            val memos = repository.getAllMemos()
            launch(Dispatchers.Main) {
                assertTrue(memos.isEmpty())
            }
        }
    }

    @After
    fun closeDB() {
        db.close()
    }
}