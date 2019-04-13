package com.furianrt.mydiary.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteCacheEntry(
        val title: String = "",
        val content: String = "",
        var current: Boolean = false
) : Parcelable