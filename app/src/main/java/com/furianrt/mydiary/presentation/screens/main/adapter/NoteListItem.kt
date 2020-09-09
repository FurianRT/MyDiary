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

import com.furianrt.mydiary.model.entity.MyNoteWithProp

sealed class NoteListItem(open val selected: Boolean) {

    fun getTypeId() = when (this) {
        is Date -> Date.TYPE_ID
        is WithImage -> WithImage.TYPE_ID
        is WithText -> WithText.TYPE_ID
    }

    data class Date(val time: Long, override val selected: Boolean = false) : NoteListItem(selected) {
        companion object {
            const val TYPE_ID = 0
        }
    }

    data class WithImage(val note: MyNoteWithProp, override val selected: Boolean) : NoteListItem(selected) {
        companion object {
            const val TYPE_ID = 1
        }
    }

    data class WithText(val note: MyNoteWithProp, override val selected: Boolean) : NoteListItem(selected) {
        companion object {
            const val TYPE_ID = 2
        }
    }
}