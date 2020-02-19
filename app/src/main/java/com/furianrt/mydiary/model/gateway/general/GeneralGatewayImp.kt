/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.general

import com.furianrt.mydiary.R
import com.furianrt.mydiary.model.entity.SyncProgressMessage
import com.furianrt.mydiary.model.source.preferences.PreferencesHelper
import com.google.gson.Gson
import javax.inject.Inject

class GeneralGatewayImp @Inject constructor(
        private val prefs: PreferencesHelper
) : GeneralGateway {

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

    override fun isNeedDefaultValues(): Boolean = prefs.isNeedDefaultValues()

    override fun setNeedDefaultValues(need: Boolean) {
        prefs.setNeedDefaultValues(need)
    }

    override fun getAppFontStyle(): Int = when(prefs.getAppFontStyle()) {
        1 -> R.style.ArimaMaduraiFontStyle
        2 -> R.style.BadScriptFontStyle
        3 -> R.style.CaveatFontStyle
        4 -> R.style.GabrielaFontStyle
        5 -> R.style.IbmPlexMonoFlowerFontStyle
        6 -> R.style.NotoSerifFontStyle
        7 -> R.style.PoorStoryfFontStyle
        8 -> R.style.PoppinsLightFontStyle
        9 -> R.style.ProductSansLightFontStyle
        10 -> R.style.RobotoFontStyle
        11 -> R.style.RobotoSlabFontStyle
        12 -> R.style.RobotoMediumFontStyle
        13 -> R.style.UbuntuFontStyle
        else ->  R.style.DefaultFontStyle
    }
}