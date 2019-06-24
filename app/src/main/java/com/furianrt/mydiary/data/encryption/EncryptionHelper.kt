package com.furianrt.mydiary.data.encryption

interface EncryptionHelper {
    fun encryptString(string: String): String
    fun decryptString(string: String): String
}