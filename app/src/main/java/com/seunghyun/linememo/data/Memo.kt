package com.seunghyun.linememo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.seunghyun.linememo.ui.edit.ImageItem
import java.io.Serializable

@Entity
data class Memo(
    @PrimaryKey val createdMillis: Long,
    @ColumnInfo(name = "last_edit") val lastEditMillis: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "images") val images: List<ImageItem>
) : Serializable {
    fun isValid() = title.isNotBlank()
    fun isNotValid() = !isValid()
}