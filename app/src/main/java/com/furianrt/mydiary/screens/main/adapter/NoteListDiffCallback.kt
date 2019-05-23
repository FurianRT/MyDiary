package com.furianrt.mydiary.screens.main.adapter

import androidx.recyclerview.widget.DiffUtil

class NoteListDiffCallback : DiffUtil.ItemCallback<NoteListItem>() {

    override fun areContentsTheSame(oldItem: NoteListItem, newItem: NoteListItem): Boolean {
        if (oldItem is NoteListContent && newItem is NoteListContent)
            return oldItem.note == newItem.note &&
                    oldItem.note.images == newItem.note.images &&
                    oldItem.note.tags == newItem.note.tags
        if (oldItem is NoteListHeader && newItem is NoteListHeader)
            return oldItem.time == newItem.time
        return false
    }

    override fun areItemsTheSame(oldItem: NoteListItem, newItem: NoteListItem): Boolean {
        if (oldItem is NoteListContent && newItem is NoteListContent)
            return oldItem.note.note.id == newItem.note.note.id
        if (oldItem is NoteListHeader && newItem is NoteListHeader)
            return oldItem.time == newItem.time
        return false
    }
}