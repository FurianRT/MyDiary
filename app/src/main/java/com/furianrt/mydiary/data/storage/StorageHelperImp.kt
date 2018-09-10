package com.furianrt.mydiary.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


class StorageHelperImp(private val context: Context) : StorageHelper {

    override fun getFile(fileName: String): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val files = dir.listFiles { file -> file.name.startsWith(fileName) }

        if (files.isEmpty()) {
            throw FileNotFoundException()
        } else {
            return files[0]
        }
    }

    override fun copyImageToStorage(sourcePath: String, destFileName: String): File {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val destFile = File(dir, "$destFileName.jpg")

        val fos = FileOutputStream(destFile)

        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(sourcePath, bmOptions)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, fos)
        fos.flush()
        fos.close()

        return destFile
    }
}