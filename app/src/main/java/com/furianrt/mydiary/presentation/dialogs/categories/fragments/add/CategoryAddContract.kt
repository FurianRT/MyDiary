/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.categories.fragments.add

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface CategoryAddContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun showErrorEmptyName()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonDoneClick(categoryName: String, categoryColor: Int)
        abstract fun onButtonCancelClick()
    }
}