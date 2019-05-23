package com.furianrt.mydiary.screens.main.fragments.drawer.adapter

import android.os.Parcelable
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.data.model.MyTag
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchItem(
        val type: Int,
        val tag: MyTag? = null,
        val category: MyCategory? = null,
        val location: MyLocation? = null,
        val mood: MyMood? = null,
        val isChecked: Boolean = false
): Parcelable {
    companion object {
        const val TYPE_TAG = 3
        const val TYPE_CATEGORY = 4
        const val TYPE_LOCATION = 5
        const val TYPE_MOOD = 6
    }
}