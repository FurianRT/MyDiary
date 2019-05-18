package com.furianrt.mydiary.screens.main.adapters.searchlist

import android.os.Parcelable
import com.thoughtbot.expandablecheckrecyclerview.models.MultiCheckExpandableGroup
import kotlinx.android.parcel.Parcelize

@Parcelize
class SearchGroup(
        val groupTitle: String,
        val groupItems: List<SearchItem>
): Parcelable, MultiCheckExpandableGroup(groupTitle, groupItems)