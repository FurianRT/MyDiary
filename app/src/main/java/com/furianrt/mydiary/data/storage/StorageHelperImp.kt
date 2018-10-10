package com.furianrt.mydiary.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


class StorageHelperImp(private val context: Context) : StorageHelper {

    private val mDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun getFile(fileName: String): File {
        val files = mDirectory.listFiles { file -> file.name.startsWith(fileName) }

        if (files.isEmpty()) {
            throw FileNotFoundException()
        } else {
            return files[0]
        }
    }

    override fun deleteFile(fileName: String): Boolean = File(mDirectory, fileName).delete()

    override fun copyImageToStorage(sourcePath: String, destFileName: String): File {
        val destFile = File(mDirectory, "$destFileName.jpg")

        val fos = FileOutputStream(destFile)

        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(sourcePath, bmOptions)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 18, fos)
        fos.flush()
        fos.close()

        return destFile
    }
}