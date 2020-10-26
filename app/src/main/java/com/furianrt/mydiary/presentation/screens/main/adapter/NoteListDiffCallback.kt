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

class NoteListDiffCallback : DiffUtil.ItemCallback<NoteListItem>() {

    override fun areItemsTheSame(oldItem: NoteListItem, newItem: NoteListItem): Boolean =
            when {
                oldItem is NoteListItem.Date && newItem is NoteListItem.Date ->
                    oldItem.time == newItem.time
                oldItem is NoteListItem.WithText && newItem is NoteListItem.WithText ->
                    oldItem.note.note.id == newItem.note.note.id
                oldItem is NoteListItem.WithImage && newItem is NoteListItem.WithImage ->
                    oldItem.note.note.id == newItem.note.note.id
                else -> false
            }

    override fun areContentsTheSame(oldItem: NoteListItem, newItem: NoteListItem): Boolean =
            when {
                oldItem is NoteListItem.Date && newItem is NoteListItem.Date ->
                    oldItem.time == newItem.time
                oldItem is NoteListItem.WithText && newItem is NoteListItem.WithText ->
                    oldItem.note == newItem.note && oldItem.selected == newItem.selected
                oldItem is NoteListItem.WithImage && newItem is NoteListItem.WithImage ->
                    oldItem.note == newItem.note && oldItem.selected == newItem.selected
                else -> false
            }
}