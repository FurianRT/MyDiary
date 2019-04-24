package com.furianrt.mydiary.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class StorageHelperImp(context: Context) : StorageHelper {

    companion object {
        private const val COMPRESS_VALUE = 18
    }

    private val mDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    override fun getFile(fileName: String): File {
        val files = mDirectory!!.listFiles { file -> file.name.startsWith(fileName) }
        if (files.isEmpty()) {
            throw FileNotFoundException()
        } else {
            return files[0]
        }
    }

    override fun deleteFile(fileName: String): Boolean = File(mDirectory, fileName).delete()

    override fun deleteFiles(fileNames: List<String>): Map<String, Boolean> =
            HashMap<String, Boolean>().apply { fileNames.forEach { this[it] = deleteFile(it) } }

    override fun copyImageToStorage(sourcePath: String, destFileName: String): File {
        val destFile = File(mDirectory, "$destFileName.jpg")
        val fos = FileOutputStream(destFile)
        BitmapFactory.decodeFile(sourcePath).compress(Bitmap.CompressFormat.JPEG, COMPRESS_VALUE, fos)
        fos.flush()
        fos.close()
        return destFile
    }

    override fun copyBitmapToStorage(bitmap: Bitmap, destFileName: String): File {
        val destFile = File(mDirectory, "$destFileName.jpg")
        val fos = FileOutputStream(destFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESS_VALUE, fos)
        fos.flush()
        fos.close()
        return destFile
    }
}