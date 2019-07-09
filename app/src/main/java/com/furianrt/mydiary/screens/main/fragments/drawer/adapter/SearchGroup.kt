package com.furianrt.mydiary.screens.main.fragments.drawer.adapter

import android.os.Parcelable
import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchGroup(
        val type: Int,
        val groupTitle: String = "",
        val groupItems: List<SearchItem>
): Parcelable, MultiCheckExpandableGroup(groupTitle, groupItems) {
    companion object {
        const val TYPE_DATE = 31
        const val TYPE_TAG = 41
        const val TYPE_CATEGORY = 51
        const val TYPE_LOCATION = 61
        const val TYPE_MOOD = 71
    }
}