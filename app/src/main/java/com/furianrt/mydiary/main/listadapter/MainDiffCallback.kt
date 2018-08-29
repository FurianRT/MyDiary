package com.furianrt.mydiary.main.listadapter

import android.support.v7.util.DiffUtil

class MainDiffCallback : DiffUtil.ItemCallback<MainListItem>() {

    override fun areContentsTheSame(oldItem: MainListItem, newItem: MainListItem): Boolean {
        if (oldItem is MainContentItem && newItem is MainContentItem)
            return oldItem.note == newItem.note
        if (oldItem is MainHeaderItem && newItem is MainHeaderItem)
            return oldItem.time == newItem.time
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: MainListItem, newItem: MainListItem): Boolean {
        if (oldItem is MainContentItem && newItem is MainContentItem)
            return oldItem.note.id == newItem.note.id
        if (oldItem is MainHeaderItem && newItem is MainHeaderItem)
            return oldItem.time == newItem.time
        return oldItem == newItem
    }
}