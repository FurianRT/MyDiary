package com.furianrt.mydiary.data.repository.device

import com.furianrt.mydiary.data.model.MyLocation

interface DeviceRepository {
    fun isNetworkAvailable(): Boolean
    fun isLocationAvailable(): Boolean
    fun findLocation(listener: OnLocationFoundListener)
    fun removeLocationListener(listener: OnLocationFoundListener)

    interface OnLocationFoundListener {
        fun onLocationFound(location: MyLocation)
    }
}