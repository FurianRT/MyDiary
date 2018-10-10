package com.furianrt.mydiary.data.prefs

interface PreferencesHelper {

    fun isWeatherEnabled(): Boolean

    fun getWeatherUnits(): Int

    fun isMapEnabled(): Boolean

    fun isMoodEnabled(): Boolean

    companion object {
        const val COLOR_PRIMARY = "color_primary"
        const val COLOR_ACCENT = "color_accent"
        const val WEATHER_AVAILABILITY = "weather_activation"
        const val WEATHER_UNITS = "weather_units"
        const val MOOD_AVAILABILITY = "mood_activation"
        const val MAP_AVAILABILITY = "map_activation"
        const val SECURITY_AVAILABILITY = "security_key"
        const val SECURITY_EMAIL = "security_email"
        const val SECURITY_TIME = "security_time"
    }
}