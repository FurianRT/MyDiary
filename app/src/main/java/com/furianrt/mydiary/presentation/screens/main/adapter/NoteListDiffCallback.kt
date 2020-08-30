/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.adapter

import androidx.recyclerview.widget.DiffUtil
import com.furianrt.mydiary.presentation.screens.main.adapter.entity.BaseNoteListItem
import com.furianrt.mydiary.presentation.screens.main.adapter.entity.NoteItemDate
import com.furianrt.mydiary.presentation.screens.main.adapter.entity.NoteItemWithImage
import com.furianrt.mydiary.presentation.screens.main.adapter.entity.NoteItemWithText

class NoteListDiffCallback : DiffUtil.ItemCallback<BaseNoteListItem>() {

    override fun areItemsTheSame(oldItem: BaseNoteListItem, newItem: BaseNoteListItem): Boolean =
            when {
                oldItem is NoteItemDate && newItem is NoteItemDate ->
                    oldItem.time == newItem.time
                oldItem is NoteItemWithText && newItem is NoteItemWithText ->
                    oldItem.note.note.id == newItem.note.note.id
                oldItem is NoteItemWithImage && newItem is NoteItemWithImage ->
                    oldItem.note.note.id == newItem.note.note.id
                else -> false
            }

    override fun areContentsTheSame(oldItem: BaseNoteListItem, newItem: BaseNoteListItem): Boolean =
            when {
                oldItem is NoteItemDate && newItem is NoteItemDate ->
                    oldItem.time == newItem.time
                oldItem is NoteItemWithText && newItem is NoteItemWithText ->
                    oldItem.note == newItem.note && oldItem.selected == newItem.selected
                oldItem is NoteItemWithImage && newItem is NoteItemWithImage ->
                    oldItem.note == newItem.note && oldItem.selected == newItem.selected
                else -> false
            }
}