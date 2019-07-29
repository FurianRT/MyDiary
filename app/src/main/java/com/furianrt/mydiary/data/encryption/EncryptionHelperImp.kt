/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.encryption

import android.annotation.SuppressLint
import android.util.Base64
import com.furianrt.mydiary.BuildConfig
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class EncryptionHelperImp @Inject constructor() : EncryptionHelper {

    @SuppressLint("GetInstance")
    override fun encryptString(string: String): String {
        val keyBytes = BuildConfig.PREFS_PASSWORD.toByteArray()
        val aesKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val encrypted = cipher.doFinal(string.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    @SuppressLint("GetInstance")
    override fun decryptString(string: String): String {
        val keyBytes = BuildConfig.PREFS_PASSWORD.toByteArray()
        val aesKey = SecretKeySpec(keyBytes, "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, aesKey)
        val decryptedByteValue = cipher.doFinal(Base64.decode(string.toByteArray(), Base64.DEFAULT))
        return String(decryptedByteValue)
    }
}