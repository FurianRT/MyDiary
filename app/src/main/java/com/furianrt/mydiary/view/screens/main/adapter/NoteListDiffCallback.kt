/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.adapter

import androidx.recyclerview.widget.DiffUtil

class NoteListDiffCallback(
        private val oldList: List<NoteListItem>,
        private val newList: List<NoteListItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem is NoteListContent && newItem is NoteListContent)
            return oldItem.note.note.id == newItem.note.note.id
        if (oldItem is NoteListHeader && newItem is NoteListHeader)
            return oldItem.time == newItem.time
        return false
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        if (oldItem is NoteListContent && newItem is NoteListContent)
            return oldItem.note == newItem.note &&
                    oldItem.note.images == newItem.note.images &&
                    oldItem.note.tags == newItem.note.tags
        if (oldItem is NoteListHeader && newItem is NoteListHeader)
            return oldItem.time == newItem.time
        return false
    }
}