/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.delete.note

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface DeleteNoteContract {

    interface MvpView : BaseMvpView {
        fun closeView()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonDeleteClick(notesIds: List<String>)
        abstract fun onButtonCancelClick()
    }
}