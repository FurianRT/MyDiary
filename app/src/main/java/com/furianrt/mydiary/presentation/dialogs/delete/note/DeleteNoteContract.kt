/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.delete.note

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface DeleteNoteContract {

    interface View : BaseMvpView {
        fun closeView()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun onButtonDeleteClick(notesIds: List<String>)
        abstract fun onButtonCancelClick()
    }
}