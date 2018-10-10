package com.furianrt.mydiary.data.prefs

import android.content.Context
import android.preference.PreferenceManager

class PreferencesHelperImp(val context: Context) : PreferencesHelper {

    private val mPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getWeatherUnits(): Int =
            mPrefs.getInt(PreferencesHelper.WEATHER_UNITS, 0)

    override fun isWeatherEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.WEATHER_AVAILABILITY, true)

    override fun isMapEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.MAP_AVAILABILITY, true)

    override fun isMoodEnabled(): Boolean =
            mPrefs.getBoolean(PreferencesHelper.MOOD_AVAILABILITY, true)
}