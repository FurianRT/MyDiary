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

interface PreferencesHelper {
    fun getPrimaryColor(): Int
    fun getAccentColor(): Int
    fun isWeatherEnabled(): Boolean
    fun setWeatherEnabled(enabled: Boolean)
    fun getWeatherUnits(): Int
    fun isMapEnabled(): Boolean
    fun setMapEnabled(enabled: Boolean)
    fun isMoodEnabled(): Boolean
    fun isPanoramaEnabled(): Boolean
    fun getTextColor(): Int
    fun setTextColor(color: Int)
    fun getSurfaceTextColor(): Int
    fun setSurfaceTextColor(color: Int)
    fun getTextSize(): Int
    fun setTextSize(size: Int)
    fun getNoteBackgroundColor(): Int
    fun setNoteBackgroundColor(color: Int)
    fun getNoteTextBackgroundColor(): Int
    fun setNoteTextBackgroundColor(color: Int)
    fun is24TimeFormat(): Boolean
    fun isSortDesc(): Boolean
    fun setSortDesc(desc: Boolean)
    fun getPin(): String
    fun setPin(pin: String)
    fun setBackupEmail(email: String)
    fun getBackupEmail(): String
    fun isAuthorized(): Boolean
    fun setAuthorized(authorized: Boolean)
    fun getPasswordRequestDelay(): Long
    fun setPasswordRequestDelay(delay: Long)
    fun isPinEnabled(): Boolean
    fun setPinEnabled(enable: Boolean)
    fun isFingerprintEnabled(): Boolean
    fun setLastSyncMessage(message: String)
    fun getLastSyncMessage(): String?
    fun setLastAppLaunchTime(time: Long)
    fun getLastAppLaunchTime(): Long
    fun getDailyImageCategory(): String
    fun isDailyImageEnabled(): Boolean
    fun getNumberOfLaunches(): Int
    fun setNumberOfLaunches(count: Int)
    fun setNeedRateOffer(need: Boolean)
    fun isRateOfferEnabled(): Boolean
    fun isNeedDefaultValues(): Boolean
    fun setNeedDefaultValues(need: Boolean)
    fun getAppFontStyle(): Int

    companion object {
        const val COLOR_PRIMARY = "color_primary"
        const val COLOR_ACCENT = "color_accent"
        const val WEATHER_AVAILABILITY = "weather_activation"
        const val WEATHER_UNITS = "weather_units"
        const val MOOD_AVAILABILITY = "mood_activation"
        const val MAP_AVAILABILITY = "map_activation"
        const val PANORAMA_AVAILABILITY = "panorama_activation"
        const val TEXT_COLOR = "all_notes_text_color"
        const val SURFACE_TEXT_COLOR = "all_notes_surface_text_color"
        const val TEXT_SIZE = "all_notes_text_size"
        const val NOTE_BACKGROUND_COLOR = "all_notes_background_color"
        const val NOTE_TEXT_BACKGROUND_COLOR = "all_notes_text_background_color"
        const val TIME_FORMAT = "time_format"
        const val SECURITY_IS_AUTHORIZED = "security_is_authorized"
        const val SECURITY_EMAIL = "security_email"
        const val SECURITY_REQUEST_DELAY = "security_request_delay"
        const val SECURITY_KEY = "security_key"
        const val FINGERPRINT_STATUS = "fingerprint_status"
        const val IS_NOTE_SORT_DESC = "is_note_sort_desc"
        const val DIARY_PIN = "diary_pin"
        const val DEFAULT_TEXT_SIZE = 16
        const val DEFAULT_PIN_DELAY = "2000"
        const val DAILY_IMAGE_CATEGORY_DEFAULT = "backgrounds"
        const val LAST_PROGRESS_MESSAGE = "last_progress_message"
        const val LAST_APP_LAUNCH_TIME = "last_app_launch_time"
        const val LOAD_DAILY_IMAGE= "load_daily_image"
        const val DAILY_IMAGE_CATEGORY= "daily_image_category"
        const val NUMBER_OF_LAUNCHES = "number_of_launches"
        const val RATE_OFFER = "rate_offer"
        const val RATE_APP_PREF_BUTTON = "rate_app"
        const val REPORT_PROBLEM_PREF_BUTTON = "problem_report"
        const val RESET_NOTES_APPEARANCE_SETTINGS = "reset_notes_appearance"
        const val NEED_DEFAULT_VALUES = "need_default_values"
        const val APP_FONT_STYLE = "all_notes_text_style"
    }
}