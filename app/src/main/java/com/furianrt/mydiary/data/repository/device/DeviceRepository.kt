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

import com.furianrt.mydiary.data.model.MyLocation

interface DeviceRepository {
    fun isFingerprintHardwareSupported(): Boolean
    fun isFingerprintEnabled(): Boolean
    fun isNetworkAvailable(): Boolean
    fun isLocationAvailable(): Boolean
    fun findLocation(listener: OnLocationFoundListener)
    fun removeLocationListener(listener: OnLocationFoundListener)

    interface OnLocationFoundListener {
        fun onLocationFound(location: MyLocation)
    }
}