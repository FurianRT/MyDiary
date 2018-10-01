package com.furianrt.mydiary.main.listadapter

import androidx.recyclerview.widget.DiffUtil

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
            return oldItem.note.note.id == newItem.note.note.id
        if (oldItem is MainHeaderItem && newItem is MainHeaderItem)
            return oldItem.time == newItem.time
        return oldItem == newItem
    }
}