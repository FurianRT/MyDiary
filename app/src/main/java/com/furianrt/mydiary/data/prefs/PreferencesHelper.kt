package com.furianrt.mydiary.data.prefs

interface PreferencesHelper {

    fun isWeatherEnabled(): Boolean
    fun getWeatherUnits(): Int
    fun isMapEnabled(): Boolean
    fun isMoodEnabled(): Boolean
    fun getTextColor(): Int
    fun getTextSize(): Int
    fun getNoteBackgroundColor(): Int
    fun getNoteTextBackgroundColor(): Int
    fun getTimeFormat(): Int
    fun isSortDesc(): Boolean
    fun setSortDesc(desc: Boolean)

    companion object {
        const val COLOR_PRIMARY = "color_primary"
        const val COLOR_ACCENT = "color_accent"
        const val WEATHER_AVAILABILITY = "weather_activation"
        const val WEATHER_UNITS = "weather_units"
        const val MOOD_AVAILABILITY = "mood_activation"
        const val MAP_AVAILABILITY = "map_activation"
        const val TEXT_COLOR = "all_notes_text_color"
        const val TEXT_SIZE = "all_notes_text_size"
        const val NOTE_BACKGROUND_COLOR = "all_notes_background_color"
        const val NOTE_TEXT_BACKGROUND_COLOR = "all_notes_text_background_color"
        const val TIME_FORMAT = "time_format"
        const val SECURITY_AVAILABILITY = "security_key"
        const val SECURITY_EMAIL = "security_email"
        const val SECURITY_TIME = "security_time"
        const val IS_NOTE_SORT_DESC = "is_note_sort_desc"
    }
}