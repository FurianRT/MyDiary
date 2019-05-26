package com.furianrt.mydiary.screens.main.fragments.drawer.adapter

import android.os.Parcelable
import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup
import kotlinx.android.parcel.Parcelize

@Parcelize
class SearchGroup(
        val type: Int,
        val groupTitle: String = "",
        val groupItems: List<SearchItem>
): Parcelable, MultiCheckExpandableGroup(groupTitle, groupItems) {
    companion object {
        const val TYPE_TAG = 31
        const val TYPE_CATEGORY = 41
        const val TYPE_LOCATION = 51
        const val TYPE_MOOD = 61
    }
}