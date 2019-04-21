package com.furianrt.mydiary.data.prefs

import android.content.Context
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import com.furianrt.mydiary.R

class PreferencesHelperImp(val context: Context) : PreferencesHelper {

    companion object {
        private const val DEFAULT_TEXT_SIZE = "16"
    }

    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getWeatherUnits(): Int =
            mPrefs.getInt(PreferencesHelper.WEATHER_UNITS, 0)

    override fun isWeatherEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.WEATHER_AVAILABILITY, true)

    override fun isMapEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.MAP_AVAILABILITY, true)

    override fun isMoodEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.MOOD_AVAILABILITY, true)

    override fun getTextColor(): Int =
            mPrefs.getInt(PreferencesHelper.TEXT_COLOR,
                    ContextCompat.getColor(context, R.color.black))

    override fun getTextSize(): Int =
            mPrefs.getString(PreferencesHelper.TEXT_SIZE, DEFAULT_TEXT_SIZE)!!.toInt()

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

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun getPin(): String = mPrefs.getString(PreferencesHelper.DIARY_PIN, " ")

    override fun setPin(pin: String) {
        mPrefs.edit().putString(PreferencesHelper.DIARY_PIN, pin).apply()
    }

    override fun setBackupEmail(email: String) {
        mPrefs.edit().putString(PreferencesHelper.SECURITY_EMAIL, email).apply()
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun getBackupEmail(): String = mPrefs.getString(PreferencesHelper.SECURITY_EMAIL, "")

    override fun isAuthorized(): Boolean = mPrefs.getBoolean(PreferencesHelper.SECURITY_IS_AUTHORIZED, true)

    override fun setAuthorized(authorized: Boolean) {
        mPrefs.edit().putBoolean(PreferencesHelper.SECURITY_IS_AUTHORIZED, authorized).apply()
    }

    override fun getPasswordRequestDelay(): Long =
            mPrefs.getLong(PreferencesHelper.SECURITY_REQUEST_DELAY, 0L)

    override fun setPasswordRequestDelay(delay: Long) {
        mPrefs.edit().putLong(PreferencesHelper.SECURITY_REQUEST_DELAY, delay).apply()
    }

    override fun isPasswordEnabled(): Boolean = mPrefs.getBoolean(PreferencesHelper.SECURITY_KEY, false)

    override fun setPasswordEnabled(enable: Boolean) {
        mPrefs.edit().putBoolean(PreferencesHelper.SECURITY_KEY, enable).apply()
    }
}