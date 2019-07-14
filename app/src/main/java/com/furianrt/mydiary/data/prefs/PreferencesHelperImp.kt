package com.furianrt.mydiary.data.prefs

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.di.application.modules.app.AppContext
import com.furianrt.mydiary.utils.getThemeAccentColor
import com.furianrt.mydiary.utils.getThemePrimaryColor
import org.joda.time.DateTime
import javax.inject.Inject

class PreferencesHelperImp @Inject constructor(
        @AppContext private val context: Context
) : PreferencesHelper {

    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getPrimaryColor(): Int =
            mPrefs.getInt(PreferencesHelper.COLOR_PRIMARY, context.getThemePrimaryColor())

    override fun getAccentColor(): Int =
            mPrefs.getInt(PreferencesHelper.COLOR_ACCENT, context.getThemeAccentColor())

    override fun getWeatherUnits(): Int =
            mPrefs.getString(PreferencesHelper.WEATHER_UNITS, "0")!!.toInt()

    override fun isWeatherEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.WEATHER_AVAILABILITY, true)

    override fun isMapEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.MAP_AVAILABILITY, true)

    override fun isMoodEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.MOOD_AVAILABILITY, true)

    override fun getTextColor(): Int =
            mPrefs.getInt(PreferencesHelper.TEXT_COLOR,
                    ContextCompat.getColor(context, R.color.black))

    override fun getSurfaceTextColor(): Int =
            mPrefs.getInt(PreferencesHelper.SURFACE_TEXT_COLOR,
                    ContextCompat.getColor(context, R.color.black))

    override fun getTextSize(): Int =
            mPrefs.getString(PreferencesHelper.TEXT_SIZE, PreferencesHelper.DEFAULT_TEXT_SIZE)!!.toInt()

    override fun getNoteBackgroundColor(): Int =
            mPrefs.getInt(PreferencesHelper.NOTE_BACKGROUND_COLOR,
                    ContextCompat.getColor(context, R.color.grey_light))

    override fun getNoteTextBackgroundColor(): Int =
            mPrefs.getInt(PreferencesHelper.NOTE_TEXT_BACKGROUND_COLOR,
                    ContextCompat.getColor(context, R.color.white))

    override fun is24TimeFormat(): Boolean =
            mPrefs.getString(PreferencesHelper.TIME_FORMAT, "true")!!.toBoolean()

    override fun isSortDesc(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.IS_NOTE_SORT_DESC, true)

    override fun setSortDesc(desc: Boolean) {
        mPrefs.edit().putBoolean(PreferencesHelper.IS_NOTE_SORT_DESC, desc).apply()
    }

    override fun getPin(): String =
            mPrefs.getString(PreferencesHelper.DIARY_PIN, " ") ?: " "

    override fun setPin(pin: String) {
        mPrefs.edit().putString(PreferencesHelper.DIARY_PIN, pin).apply()
    }

    override fun setBackupEmail(email: String) {
        mPrefs.edit().putString(PreferencesHelper.SECURITY_EMAIL, email).apply()
    }

    override fun getBackupEmail(): String =
            mPrefs.getString(PreferencesHelper.SECURITY_EMAIL, "") ?: ""

    override fun isAuthorized(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.SECURITY_IS_AUTHORIZED, true)

    override fun setAuthorized(authorized: Boolean) {
        mPrefs.edit().putBoolean(PreferencesHelper.SECURITY_IS_AUTHORIZED, authorized).apply()
    }

    override fun getPasswordRequestDelay(): Long =
            mPrefs.getString(PreferencesHelper.SECURITY_REQUEST_DELAY, PreferencesHelper.DEFAULT_PIN_DELAY)!!.toLong()

    override fun setPasswordRequestDelay(delay: Long) {
        mPrefs.edit().putString(PreferencesHelper.SECURITY_REQUEST_DELAY, delay.toString()).apply()
    }

    override fun isPinEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.SECURITY_KEY, false)

    override fun setPinEnabled(enable: Boolean) {
        mPrefs.edit().putBoolean(PreferencesHelper.SECURITY_KEY, enable).apply()
    }

    override fun isFingerprintEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.FINGERPRINT_STATUS, true)

    override fun setLastSyncMessage(message: String) {
        mPrefs.edit().putString(PreferencesHelper.LAST_PROGRESS_MESSAGE, message).apply()
    }

    override fun getLastSyncMessage(): String? =
            mPrefs.getString(PreferencesHelper.LAST_PROGRESS_MESSAGE, "")

    override fun setLastAppLaunchTime(time: Long) {
        mPrefs.edit().putLong(PreferencesHelper.LAST_APP_LAUNCH_TIME, time).apply()
    }

    override fun getLastAppLaunchTime(): Long =
            mPrefs.getLong(PreferencesHelper.LAST_APP_LAUNCH_TIME, DateTime.now().millis)

    override fun getDailyImageCategory(): String =
            mPrefs.getString(PreferencesHelper.DAILY_IMAGE_CATEGORY, PreferencesHelper.DAILY_IMAGE_CATEGORY_DEFAULT)
                    ?: PreferencesHelper.DAILY_IMAGE_CATEGORY_DEFAULT

    override fun isDailyImageEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.LOAD_DAILY_IMAGE, true)

    override fun getNumberOfLaunches(): Int = mPrefs.getInt(PreferencesHelper.NUMBER_OF_LAUNCHES, 0)

    override fun setNumberOfLaunches(count: Int) {
        mPrefs.edit().putInt(PreferencesHelper.NUMBER_OF_LAUNCHES, count).apply()
    }

    override fun isNeedRateOffer(): Boolean = mPrefs.getBoolean(PreferencesHelper.RATE_OFFER, true)

    override fun setNeedRateOffer(need: Boolean) {
        mPrefs.edit().putBoolean(PreferencesHelper.RATE_OFFER, need).apply()
    }
}