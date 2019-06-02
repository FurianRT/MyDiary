package com.furianrt.mydiary.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SyncProgressMessage(
        val taskIndex: Int,
        val progress: Int = 0,
        val totalTasks: Int = 8,
        var message: String? = null,
        val hasError: Boolean = false
) : Parcelable {
    companion object {
        const val PROFILE_CHECK = 0
        const val SYNC_NOTES = 1
        const val SYNC_APPEARANCE = 2
        const val SYNC_CATEGORIES = 3
        const val SYNC_TAGS = 4
        const val SYNC_NOTE_TAGS = 5
        const val SYNC_LOCATION = 6
        const val SYNC_FORECAST = 7
        const val SYNC_NOTE_LOCATIONS = 8
        const val SYNC_IMAGES = 9
        const val CLEANUP = 10
        const val SYNC_FINISHED = 11
        const val UNKNOWN = -1
    }
}