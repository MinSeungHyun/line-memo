package com.seunghyun.linememo.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

fun Context.copyImageToStorage(uri: Uri): Uri {
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(contentResolver, uri)
    }
    return saveBitmapToStorage(bitmap)
}

fun Context.saveBitmapToStorage(bitmap: Bitmap): Uri {
    val path = getImagePathForCurrent()
    val fos = FileOutputStream(path)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    return Uri.parse(path.path)
}

fun Context.getImagePathForCurrent() = File(filesDir, "${System.currentTimeMillis()}.jpg")
