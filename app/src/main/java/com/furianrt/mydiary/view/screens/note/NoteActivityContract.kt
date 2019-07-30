/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.note

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface NoteActivityContract {

    interface MvpView : BaseMvpView {
        fun showNotes(noteIds: List<String>)
        fun closeView()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(noteId: String, newNote: Boolean)
    }
}