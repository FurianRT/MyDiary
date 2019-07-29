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

import com.furianrt.mydiary.R

data class NoteListHeader(var time: Long) : NoteListItem() {

    override fun getType(): Int = R.layout.activity_main_list_header
}