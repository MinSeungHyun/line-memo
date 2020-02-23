package com.seunghyun.linememo.data

import android.content.Context
import androidx.room.*
import com.google.gson.Gson
import com.seunghyun.linememo.ui.edit.ImageItem

@Database(entities = [Memo::class], version = 1)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao

    companion object {
        const val DB_NAME = "linememo-db"
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context)
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        ).build()
    }
}

private class Converter {
    @TypeConverter
    fun imageListToString(images: List<ImageItem>): String =
        Gson().toJson(images)

    @TypeConverter
    fun stringToImageList(imagesString: String): List<ImageItem> =
        Gson().fromJson(imagesString, arrayOf<ImageItem>().javaClass).toList()
}