package com.furianrt.mydiary.general

import android.content.Context
import android.os.Bundle
import com.furianrt.mydiary.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics

object Analytics {
    const val EVENT_HEADER_IMAGE_SETTINGS = "header_image_settings"
    const val EVENT_MAIN_SETTINGS = "main_settings"
    const val EVENT_NOTE_SETTINGS = "note_settings"
    const val EVENT_PROFILE_SETTINGS = "profile_settings"
    const val EVENT_NOTE_ADDED = "note_added"
    const val EVENT_NOTE_DELETED = "note_deleted"
    const val EVENT_SYNC_COMPLETED = "sync_completed"
    const val EVENT_SYNC_FAILED = "sync_failed"
    const val EVENT_PREMIUM_PURSHASED = "premium_purshased"
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
    const val EVENT_NORE_REDO = "note_redo"
    const val EVENT_NOTE_DATE_CHANGED = "note_date_changed"
    const val EVENT_NOTE_TIME_CHANGED = "note_time_changed"
    const val EVENT_NOTE_SHARE = "note_share"
    const val EVENT_NOTE_IMAGE_PAGER_OPENED = "note_image_opened"
    const val EVENT_NOTE_IMAGE_PAGER_IMAGE_EDITED = "note_image_page_image_edited"
    const val EVENT_NOTE_IMAGE_PAGER_IMAGE_DELETE = "note_image_page_image_delete"
    const val EVENT_NOTE_IMAGE_LIST_OPENED = "note_image_list_opened"
    const val EVENT_NOTE_IMAGE_LIST_DRAG_DELETE = "note_image_list_drag_delete"
    const val EVENT_NOTE_IMAGE_LIST_IMAGE_DELETE = "note_image_list_image_delete"
    const val EVENT_NOTE_IMAGE_LIST_IMAGE_ADD = "note_image_list_image_added"
    const val EVENT_NOTE_BACKGROUD_CHANGED = "note_background_changed"
    const val EVENT_NOTE_TEXT_BACKGROUD_CHANGED = "note_text_background_changed"
    const val EVENT_NOTE_TEXT_COLOR_CHANGED = "note_text_color_changed"
    const val EVENT_NOTE_SURFACE_TEXT_COLOR_CHANGED = "note_surface_text_color_changed"
    const val EVENT_NOTE_TEXT_SIZE_CHANGED = "note_text_size_changed"
    const val EVENT_PRIMARY_COLOR_CHANGED = "primary_color_changed"
    const val EVENT_ACCENT_COLOR_CHANGED = "accent_color_changed"
    const val EVENT_PIN_CREATED = "pin_created"
    const val EVENT_PIN_REMOVED = "pin_removed"
    const val EVENT_UNDO_ERROR = "undo_error"
    const val EVENT_REDO_ERROR = "redo_error"

    fun sendEvent(context: Context, event: String, bundle: Bundle? = null) {
        if (!BuildConfig.DEBUG) {
            FirebaseAnalytics.getInstance(context).logEvent(event, bundle)
        }
    }
}