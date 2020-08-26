/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.device

import android.graphics.Bitmap
import com.anjlab.android.iab.v3.BillingProcessor
import com.furianrt.mydiary.model.entity.MyLocation
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single

interface DeviceGateway : BillingProcessor.IBillingHandler {
    fun isFingerprintEnabled(): Boolean
    fun isNetworkAvailable(): Boolean
    fun isLocationAvailable(): Boolean
    fun findLocation(): Maybe<MyLocation>
    fun isItemPurchased(productId: String): Boolean
    fun getTutorialNoteMoodId(): Int
    fun getTutorialNoteTitle(): String
    fun getTutorialNoteContent(): String
    fun getTutorialNoteBitmap(): Bitmap
    fun getRealPathFromUri(uri: String, callback: OnUriConvertCallback)
    fun clearUriTempFiles()
    fun isUpdateAvailable(): Single<Boolean>

    interface OnUriConvertCallback {
        fun onUriRealPathReceived(path: String)
        fun onUriRealPathError()
    }
}