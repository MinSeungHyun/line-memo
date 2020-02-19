package com.seunghyun.linememo.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.seunghyun.linememo.ui.edit.ImageItem

@Entity
data class Memo(
    @PrimaryKey val createdMillis: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "images") val images: List<ImageItem>
)