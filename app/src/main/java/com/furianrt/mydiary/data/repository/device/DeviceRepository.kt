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

import com.anjlab.android.iab.v3.BillingProcessor
import com.furianrt.mydiary.data.model.MyLocation

interface DeviceRepository : BillingProcessor.IBillingHandler {
    fun isFingerprintHardwareSupported(): Boolean
    fun isFingerprintEnabled(): Boolean
    fun isNetworkAvailable(): Boolean
    fun isLocationAvailable(): Boolean
    fun findLocation(listener: OnLocationFoundListener)
    fun removeLocationListener(listener: OnLocationFoundListener)
    fun isItemPurchased(productId: String): Boolean

    interface OnLocationFoundListener {
        fun onLocationFound(location: MyLocation)
    }
}