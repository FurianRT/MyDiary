package com.furianrt.mydiary.data.repository.general

import com.furianrt.mydiary.data.model.SyncProgressMessage

interface GeneralRepository {
    fun is24TimeFormat(): Boolean
    fun setLastAppLaunchTime(time: Long)
    fun getLastAppLaunchTime(): Long
    fun getNumberOfLaunches(): Int
    fun setNumberOfLaunches(count: Int)
    fun setLastSyncMessage(message: SyncProgressMessage?)
    fun getLastSyncMessage(): SyncProgressMessage?
    fun enableRateOffer(enable: Boolean)
    fun isRateOfferEnabled(): Boolean
    fun getPrimaryColor(): Int
    fun getAccentColor(): Int
}