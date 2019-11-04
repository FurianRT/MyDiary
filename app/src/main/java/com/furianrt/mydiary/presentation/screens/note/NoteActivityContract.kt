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

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface NoteActivityContract {

    interface MvpView : BaseMvpView {
        fun showNotes(noteIds: List<String>)
        fun closeView()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(noteId: String, newNote: Boolean)
    }
}