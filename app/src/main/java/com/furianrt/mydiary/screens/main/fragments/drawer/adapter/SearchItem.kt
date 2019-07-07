package com.furianrt.mydiary.screens.main.fragments.drawer.adapter

import android.os.Parcelable
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.data.model.MyTag
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate

@Parcelize
data class SearchItem(
        val type: Int,
        val dateColors: Map<LocalDate, Set<Int>>? = null,
        val tag: MyTag? = null,
        val category: MyCategory? = null,
        val location: MyLocation? = null,
        val mood: MyMood? = null,
        val isChecked: Boolean = false
): Parcelable {
    companion object {
        const val TYPE_DATE = 3
        const val TYPE_TAG = 4
        const val TYPE_CATEGORY = 5
        const val TYPE_MOOD = 6
        const val TYPE_LOCATION = 7
        const val TYPE_NO_TAGS = 8
        const val TYPE_NO_CATEGORY = 9
        const val TYPE_NO_MOOD = 10
        const val TYPE_NO_LOCATION = 11
    }
}