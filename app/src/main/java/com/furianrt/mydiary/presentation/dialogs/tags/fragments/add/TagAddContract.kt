/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.add

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface TagAddContract {

    interface MvpView : BaseMvpView {
        fun closeView()
        fun showErrorEmptyTagName()
        fun showErrorExistingTagName()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(noteId: String)
        abstract fun onButtonAddClick(name: String)
        abstract fun onButtonCloseClick()
    }
}