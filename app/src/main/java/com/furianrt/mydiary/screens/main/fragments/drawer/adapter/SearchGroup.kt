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
        const val TYPE_TAG = 7
        const val TYPE_CATEGORY = 8
        const val TYPE_LOCATION = 9
        const val TYPE_MOOD = 10
    }
}