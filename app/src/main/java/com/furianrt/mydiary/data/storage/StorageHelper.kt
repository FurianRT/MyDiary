/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.storage

import android.graphics.Bitmap
import java.io.File

interface StorageHelper {

    fun getFile(fileName: String): File
    fun copyImageToStorage(sourcePath: String, destFileName: String): File
    fun copyBitmapToStorage(bitmap: Bitmap, destFileName: String): File
    fun deleteFile(fileName: String): Boolean
    fun deleteFiles(fileNames: List<String>): Map<String, Boolean>
}