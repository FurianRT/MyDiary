/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.adapter.entity

import com.furianrt.mydiary.model.entity.MyNoteWithProp

data class NoteItemWithText(
        val note: MyNoteWithProp,
        override val selected: Boolean
) : BaseNoteListItem(selected)