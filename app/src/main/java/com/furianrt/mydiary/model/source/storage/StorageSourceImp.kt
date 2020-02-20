/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.storage

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.furianrt.mydiary.di.application.modules.app.AppContext
import id.zelory.compressor.Compressor
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class StorageSourceImp @Inject constructor(@AppContext context: Context) : StorageSource {

    companion object {
        private const val COMPRESS_VALUE_BITMAP = 18
        private const val COMPRESS_VALUE_IMAGE = 50
    }

    private val mCompressor = Compressor(context)
    private val mExternalDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private val mInternalDir = context.filesDir
    private val mAvailableDir = if (mExternalDir != null && isExternalStorageWritable()) {
        mExternalDir
    } else {
        mInternalDir
    }

    override fun deleteFile(fileName: String): Boolean =
            if (mExternalDir != null && File(mExternalDir, fileName).delete()) {
                true
            } else {
                File(mInternalDir, fileName).delete()
            }

    override fun deleteFiles(fileNames: List<String>): Map<String, Boolean> =
            HashMap<String, Boolean>().apply { fileNames.forEach { this[it] = deleteFile(it) } }

    override fun copyImageToStorage(sourcePath: String, destFileName: String): File =
            mCompressor
                    .setQuality(COMPRESS_VALUE_IMAGE)
                    .setCompressFormat(Bitmap.CompressFormat.WEBP)
                    .setDestinationDirectoryPath("$mAvailableDir/$destFileName.webp")
                    .compressToFile(File(sourcePath), "")

    override fun copyBitmapToStorage(bitmap: Bitmap, destFileName: String): File {
        val destFile = File(mAvailableDir, "$destFileName.webp")
        val fos = FileOutputStream(destFile)
        bitmap.compress(Bitmap.CompressFormat.WEBP, COMPRESS_VALUE_BITMAP, fos)
        fos.flush()
        fos.close()
        return destFile
    }

    override fun getAvailablePictureDirectory(): String = mAvailableDir.toURI().toString()

    private fun isExternalStorageWritable(): Boolean =
            Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
}