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
import com.furianrt.mydiary.view.screens.main.adapter.NoteListAdapter.*

class NoteListDiffCallback(
        private val oldList: List<NoteItemView>,
        private val newList: List<NoteItemView>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            oldItem.type == NoteItemView.TYPE_HEADER && oldItem.type == NoteItemView.TYPE_HEADER ->
                oldItem.time == newItem.time
            oldItem.type == NoteItemView.TYPE_NOTE_WITH_TEXT && oldItem.type == NoteItemView.TYPE_NOTE_WITH_TEXT ->
                oldItem.note?.note?.id == newItem.note?.note?.id
            oldItem.type == NoteItemView.TYPE_NOTE_WITH_IMAGE && oldItem.type == NoteItemView.TYPE_NOTE_WITH_IMAGE ->
                oldItem.note?.note?.id == newItem.note?.note?.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when {
            oldItem.type == NoteItemView.TYPE_HEADER && oldItem.type == NoteItemView.TYPE_HEADER ->
                oldItem.time == newItem.time
            oldItem.type == NoteItemView.TYPE_NOTE_WITH_TEXT && oldItem.type == NoteItemView.TYPE_NOTE_WITH_TEXT ->
                oldList[oldItemPosition] == newList[newItemPosition]
            oldItem.type == NoteItemView.TYPE_NOTE_WITH_IMAGE && oldItem.type == NoteItemView.TYPE_NOTE_WITH_IMAGE ->
                oldList[oldItemPosition] == newList[newItemPosition]
            else -> false
        }
    }
}