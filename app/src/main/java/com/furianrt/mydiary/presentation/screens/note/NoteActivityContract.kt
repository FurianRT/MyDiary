/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note

import com.furianrt.mydiary.model.entity.MyNoteWithImages
import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface NoteActivityContract {

    interface View : BaseView {
        fun showNotes(notes: List<MyNoteWithImages>)
        fun closeView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(noteId: String, newNote: Boolean)
    }
}