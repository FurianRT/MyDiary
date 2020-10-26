/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.preferences

import android.content.Context
import androidx.preference.PreferenceManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.furianrt.mydiary.utils.getColorCompat
import com.furianrt.mydiary.utils.getThemeAccentColor
import com.furianrt.mydiary.utils.getThemePrimaryColor
import javax.inject.Inject

class PreferencesSourceImp @Inject constructor(
        @AppContext private val context: Context
) : PreferencesSource {

    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getPrimaryColor(): Int =
            mPrefs.getInt(PreferencesSource.COLOR_PRIMARY, context.getThemePrimaryColor())

    override fun getAccentColor(): Int =
            mPrefs.getInt(PreferencesSource.COLOR_ACCENT, context.getThemeAccentColor())

    override fun getWeatherUnits(): Int =
            mPrefs.getString(PreferencesSource.WEATHER_UNITS, "0")!!.toInt()

    override fun isWeatherEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesSource.WEATHER_AVAILABILITY, true)

    override fun isMapEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesSource.MAP_AVAILABILITY, true)

    override fun setMapEnabled(enabled: Boolean) {
        mPrefs.edit().putBoolean(PreferencesSource.MAP_AVAILABILITY, enabled).apply()
    }

    override fun isMoodEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesSource.MOOD_AVAILABILITY, true)

    override fun getTextColor(): Int =
            mPrefs.getInt(PreferencesSource.TEXT_COLOR, context.getColorCompat(R.color.black))

    override fun setTextColor(color: Int) {
        mPrefs.edit().putInt(PreferencesSource.TEXT_COLOR, color).apply()
    }

    override fun getSurfaceTextColor(): Int =
            mPrefs.getInt(PreferencesSource.SURFACE_TEXT_COLOR, context.getColorCompat(R.color.black))

    override fun setSurfaceTextColor(color: Int) {
        mPrefs.edit().putInt(PreferencesSource.SURFACE_TEXT_COLOR, color).apply()
    }

    override fun getTextSize(): Int =
            mPrefs.getString(PreferencesSource.TEXT_SIZE, PreferencesSource.DEFAULT_TEXT_SIZE.toString())!!.toInt()

    override fun setTextSize(size: Int) {
        mPrefs.edit().putString(PreferencesSource.TEXT_SIZE, size.toString()).apply()
    }

    override fun getNoteBackgroundColor(): Int =
            mPrefs.getInt(PreferencesSource.NOTE_BACKGROUND_COLOR, context.getColorCompat(R.color.grey_light))

    override fun setNoteBackgroundColor(color: Int) {
        mPrefs.edit().putInt(PreferencesSource.NOTE_BACKGROUND_COLOR, color).apply()
    }

    override fun getNoteTextBackgroundColor(): Int =
            mPrefs.getInt(PreferencesSource.NOTE_TEXT_BACKGROUND_COLOR, context.getColorCompat(R.color.white))

    override fun setNoteTextBackgroundColor(color: Int) {
        mPrefs.edit().putInt(PreferencesSource.NOTE_TEXT_BACKGROUND_COLOR, color).apply()
    }

    override fun is24TimeFormat(): Boolean =
            mPrefs.getString(PreferencesSource.TIME_FORMAT, "true")!!.toBoolean()

    override fun isSortDesc(): Boolean =
            mPrefs.getBoolean(PreferencesSource.IS_NOTE_SORT_DESC, true)

    override fun setSortDesc(desc: Boolean) {
        mPrefs.edit().putBoolean(PreferencesSource.IS_NOTE_SORT_DESC, desc).apply()
    }

    override fun getPin(): String =
            mPrefs.getString(PreferencesSource.DIARY_PIN, " ") ?: " "

    override fun setPin(pin: String) {
        mPrefs.edit().putString(PreferencesSource.DIARY_PIN, pin).apply()
    }

    override fun setBackupEmail(email: String) {
        mPrefs.edit().putString(PreferencesSource.SECURITY_EMAIL, email).apply()
    }

    override fun getBackupEmail(): String =
            mPrefs.getString(PreferencesSource.SECURITY_EMAIL, "") ?: ""

    override fun isAuthorized(): Boolean =
            mPrefs.getBoolean(PreferencesSource.SECURITY_IS_AUTHORIZED, true)

    override fun setAuthorized(authorized: Boolean) {
        mPrefs.edit().putBoolean(PreferencesSource.SECURITY_IS_AUTHORIZED, authorized).apply()
    }

    override fun getPasswordRequestDelay(): Long =
            mPrefs.getString(PreferencesSource.SECURITY_REQUEST_DELAY, PreferencesSource.DEFAULT_PIN_DELAY)!!.toLong()

    override fun setPasswordRequestDelay(delay: Long) {
        mPrefs.edit().putString(PreferencesSource.SECURITY_REQUEST_DELAY, delay.toString()).apply()
    }

    override fun isPinEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesSource.SECURITY_KEY, false)

    override fun setPinEnabled(enable: Boolean) {
        mPrefs.edit().putBoolean(PreferencesSource.SECURITY_KEY, enable).apply()
    }

    override fun isFingerprintEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesSource.FINGERPRINT_STATUS, true)

    override fun setLastSyncMessage(message: String) {
        mPrefs.edit().putString(PreferencesSource.LAST_PROGRESS_MESSAGE, message).apply()
    }

    override fun getLastSyncMessage(): String? =
            mPrefs.getString(PreferencesSource.LAST_PROGRESS_MESSAGE, "")

    override fun setLastAppLaunchTime(time: Long) {
        mPrefs.edit().putLong(PreferencesSource.LAST_APP_LAUNCH_TIME, time).apply()
    }

    override fun getLastAppLaunchTime(): Long =
            mPrefs.getLong(PreferencesSource.LAST_APP_LAUNCH_TIME, System.currentTimeMillis())

    override fun getDailyImageCategory(): String =
            mPrefs.getString(PreferencesSource.DAILY_IMAGE_CATEGORY, PreferencesSource.DAILY_IMAGE_CATEGORY_DEFAULT)
                    ?: PreferencesSource.DAILY_IMAGE_CATEGORY_DEFAULT

    override fun isDailyImageEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesSource.LOAD_DAILY_IMAGE, true)

    override fun getNumberOfLaunches(): Int = mPrefs.getInt(PreferencesSource.NUMBER_OF_LAUNCHES, 1)

    override fun setNumberOfLaunches(count: Int) {
        mPrefs.edit().putInt(PreferencesSource.NUMBER_OF_LAUNCHES, count).apply()
    }

    override fun isRateOfferEnabled(): Boolean = mPrefs.getBoolean(PreferencesSource.RATE_OFFER, true)

    override fun setNeedRateOffer(need: Boolean) {
        mPrefs.edit().putBoolean(PreferencesSource.RATE_OFFER, need).apply()
    }

    override fun isNeedDefaultValues(): Boolean = mPrefs.getBoolean(PreferencesSource.NEED_DEFAULT_VALUES, true)

    override fun setNeedDefaultValues(need: Boolean) {
        mPrefs.edit().putBoolean(PreferencesSource.NEED_DEFAULT_VALUES, need).apply()
    }

    override fun getAppFontStyle(): Int =
            mPrefs.getString(PreferencesSource.APP_FONT_STYLE, "0")?.toInt() ?: 0
}