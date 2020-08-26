/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.analytics

import android.os.Bundle

interface MyAnalytics {

    fun init()
    fun sendEvent(event: String, bundle: Bundle? = null)
    fun logExceptionEvent(error: Throwable)

    companion object {
        //Events
        const val EVENT_HEADER_IMAGE_SETTINGS = "header_image_settings"
        const val EVENT_MAIN_SETTINGS = "main_settings"
        const val EVENT_NOTE_SETTINGS = "note_settings"
        const val EVENT_PROFILE_SETTINGS = "profile_settings"
        const val EVENT_NOTE_ADDED = "note_added"
        const val EVENT_NOTE_DELETED = "note_deleted"
        const val EVENT_SYNC_COMPLETED = "sync_completed"
        const val EVENT_SYNC_FAILED = "sync_failed"
        const val EVENT_PREMIUM_PURCHASED = "premium_purchased"
        const val EVENT_SIGN_IN = "sign_in"
        const val EVENT_SIGNED_IN = "signed_in"
        const val EVENT_SIGN_UP = "sign_up"
        const val EVENT_SIGNED_UP = "signed_up"
        const val EVENT_PASSWORD_CHANGE = "password_change"
        const val EVENT_PASSWORD_CHANGED = "password_changed"
        const val EVENT_SIGN_OUT = "sign_out"
        const val EVENT_PROFILE_ABOUT = "profile_about"
        const val EVENT_FORGOT_PASSWORD = "forgot_password"
        const val EVENT_FORGOT_PIN = "forgot_pin"
        const val EVENT_FINGERPRINT_SUCCESS = "fingerprint_success"
        const val EVENT_NOTE_OPENED = "note_opened"
        const val EVENT_NOTE_TAG_EDITED = "note_tag_edited"
        const val EVENT_NOTE_TAG_DELETED = "note_tag_deleted"
        const val EVENT_NOTE_CATEGORY_EDITED = "note_category_edited"
        const val EVENT_NOTE_CATEGORY_DELETED = "note_category_deleted"
        const val EVENT_NOTE_MOOD_CHANGED = "note_mood_changed"
        const val EVENT_SPEECH_TO_TEXT = "speech_to_text"
        const val EVENT_NOTE_UNDO = "note_undo"
        const val EVENT_NOTE_REDO = "note_redo"
        const val EVENT_NOTE_DATE_CHANGED = "note_date_changed"
        const val EVENT_NOTE_TIME_CHANGED = "note_time_changed"
        const val EVENT_NOTE_IMAGE_PAGER_OPENED = "note_image_opened"
        const val EVENT_NOTE_IMAGE_PAGER_IMAGE_EDITED = "note_image_page_image_edited"
        const val EVENT_NOTE_IMAGE_PAGER_IMAGE_DELETE = "note_image_page_image_delete"
        const val EVENT_NOTE_IMAGE_LIST_OPENED = "note_image_list_opened"
        const val EVENT_NOTE_IMAGE_LIST_DRAG_DELETE = "note_image_list_drag_delete"
        const val EVENT_NOTE_IMAGE_LIST_IMAGE_DELETE = "note_image_list_image_delete"
        const val EVENT_NOTE_IMAGE_LIST_IMAGE_ADD = "note_image_list_image_added"
        const val EVENT_NOTE_IMAGE_LIST_TAKE_PHOTO = "note_image_list_take_photo"
        const val EVENT_NOTE_BACKGROUND_CHANGED = "note_background_changed"
        const val EVENT_NOTE_TEXT_BACKGROUND_CHANGED = "note_text_background_changed"
        const val EVENT_NOTE_TEXT_COLOR_CHANGED = "note_text_color_changed"
        const val EVENT_NOTE_SURFACE_TEXT_COLOR_CHANGED = "note_surface_text_color_changed"
        const val EVENT_NOTE_TEXT_SIZE_CHANGED = "note_text_size_changed"
        const val EVENT_PRIMARY_COLOR_CHANGED = "primary_color_changed"
        const val EVENT_ACCENT_COLOR_CHANGED = "accent_color_changed"
        const val EVENT_FONT_STYLE_CHANGED = "font_style_changed"
        const val EVENT_PIN_CREATED = "pin_created"
        const val EVENT_PIN_REMOVED = "pin_removed"
        const val EVENT_SEARCH_WORD_OPENED = "search_word_opened"
        const val EVENT_SEARCH_TAG_CHANGED = "search_tag_changed"
        const val EVENT_SEARCH_CATEGORY_CHANGED = "search_category_changed"
        const val EVENT_SEARCH_LOCATION_CHANGED = "search_location_changed"
        const val EVENT_SEARCH_MOOD_CHANGED = "search_mood_changed"
        const val EVENT_SEARCH_NO_TAGS_CHANGED = "search_tags_changed"
        const val EVENT_SEARCH_NO_CATEGORY_CHANGED = "search_category_changed"
        const val EVENT_SEARCH_NO_MOOD_CHANGED = "search_mood_changed"
        const val EVENT_SEARCH_NO_LOCATION_CHANGED = "search_location_changed"
        const val EVENT_SEARCH_DATE_CHANGED = "search_date_changed"
        const val EVENT_SHARE_NOTE = "share_note"
        const val EVENT_DAILY_IMAGE_OPEN = "daily_image_open"
        const val EVENT_DAILY_IMAGE_TURN_OFF = "daily_image_turn_off"
        const val EVENT_DAILY_IMAGE_TURN_ON = "daily_image_turn_on"
        const val EVENT_DAILY_IMAGE_CATEGORY_CHANGED = "daily_image_category_changed"
        const val EVENT_RICH_TEXT_BOLD_APPLIED = "rich_text_bold_applied"
        const val EVENT_RICH_TEXT_ITALIC_APPLIED = "rich_text_italic_applied"
        const val EVENT_RICH_TEXT_STRIKETHROUGH_APPLIED = "rich_text_strikethrough_applied"
        const val EVENT_RICH_TEXT_BIG_TEXT_APPLIED = "rich_text_big_text_applied"
        const val EVENT_RICH_TEXT_COLOR_APPLIED = "rich_text_color_applied"
        const val EVENT_RICH_TEXT_FILL_COLOR_APPLIED = "rich_text_fill_color_applied"

        //Error events
        const val EVENT_UNDO_ERROR = "undo_error"
        const val EVENT_REDO_ERROR = "redo_error"
        const val EVENT_DAILY_IMAGE_LOAD_ERROR = "daily_image_load_error"
        const val EVENT_IMAGE_SAVE_ERROR = "image_save_error"
        const val EVENT_IMAGE_COPY_ERROR = "image_copy_error"
        const val EVENT_FORECAST_LOAD_ERROR = "forecast_load_error"
        const val EVENT_GALLERY_NOT_FOUND_ERROR = "gallery_not_found_error"

        //Event parameters
        const val BUNDLE_TASK_INDEX = "task_index"
        const val BUNDLE_CATEGORY = "category"
        const val BUNDLE_ERROR_TEXT = "error_text"
    }
}