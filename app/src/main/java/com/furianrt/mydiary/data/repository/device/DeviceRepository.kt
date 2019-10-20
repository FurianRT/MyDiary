/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.repository.device

import android.graphics.Bitmap
import com.anjlab.android.iab.v3.BillingProcessor
import com.furianrt.mydiary.data.entity.MyLocation
import com.hbisoft.pickit.PickiTCallbacks

interface DeviceRepository : BillingProcessor.IBillingHandler, PickiTCallbacks {
    fun isFingerprintEnabled(): Boolean
    fun isNetworkAvailable(): Boolean
    fun isLocationAvailable(): Boolean
    fun findLocation(callback: OnLocationFoundCallback)
    fun removeLocationCallback(callback: OnLocationFoundCallback)
    fun isItemPurchased(productId: String): Boolean
    fun getTutorialNoteMoodId(): Int
    fun getTutorialNoteTitle(): String
    fun getTutorialNoteContent(): String
    fun getTutorialNoteBitmap(): Bitmap
    fun getRealPathFromUri(uri: String, callback: OnUriConvertCallback)
    fun removeUriConvertCallback(callback: OnUriConvertCallback)
    fun clearUriTempFiles()

    interface OnLocationFoundCallback {
        fun onLocationFound(location: MyLocation)
    }

    interface OnUriConvertCallback {
        fun onUriRealPathReceived(path: String)
        fun onUriRealPathError()
    }
}