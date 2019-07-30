/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.repository.general

import com.furianrt.mydiary.data.model.SyncProgressMessage
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.google.gson.Gson
import javax.inject.Inject

class GeneralRepositoryImp @Inject constructor(
        private val prefs: PreferencesHelper
) : GeneralRepository {

    override fun is24TimeFormat(): Boolean = prefs.is24TimeFormat()

    override fun getLastAppLaunchTime(): Long = prefs.getLastAppLaunchTime()

    override fun setLastAppLaunchTime(time: Long) {
        prefs.setLastAppLaunchTime(time)
    }

    override fun getNumberOfLaunches(): Int = prefs.getNumberOfLaunches()

    override fun setNumberOfLaunches(count: Int) {
        prefs.setNumberOfLaunches(count)
    }

    override fun getLastSyncMessage(): SyncProgressMessage? {
        val message = prefs.getLastSyncMessage()
        return if (message.isNullOrBlank()) {
            null
        } else {
            Gson().fromJson(message, SyncProgressMessage::class.java)
        }
    }

    override fun setLastSyncMessage(message: SyncProgressMessage?) {
        prefs.setLastSyncMessage(if (message == null) {
            ""
        } else {
            Gson().toJson(message)
        })
    }

    override fun enableRateOffer(enable: Boolean) {
        prefs.setNeedRateOffer(enable)
    }

    override fun isRateOfferEnabled(): Boolean = prefs.isRateOfferEnabled()

    override fun getPrimaryColor(): Int = prefs.getPrimaryColor()

    override fun getAccentColor(): Int = prefs.getAccentColor()
}