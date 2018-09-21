package com.furianrt.mydiary.data.storage

import java.io.File

interface StorageHelper {

    fun getFile(fileName: String): File

    fun copyImageToStorage(sourcePath: String, destFileName: String): File

    fun deleteFile(fileName: String): Boolean
}