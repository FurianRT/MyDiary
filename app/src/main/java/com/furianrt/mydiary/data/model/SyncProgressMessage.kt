/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

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
        const val SYNC_STARTED = 0
        const val SYNC_CATEGORIES = 1
        const val SYNC_TAGS = 2
        const val SYNC_NOTE_TAGS = 3
        const val SYNC_LOCATION = 4
        const val SYNC_NOTE_LOCATIONS = 5
        const val SYNC_FORECAST = 6
        const val SYNC_IMAGES = 7
        const val SYNC_APPEARANCE = 8
        const val SYNC_SPANS = 9
        const val SYNC_NOTES = 10
        const val CLEANUP = 11
        const val SYNC_FINISHED = 12
        const val UNKNOWN = -1
    }
}