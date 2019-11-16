/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.delete.image

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface DeleteImageContract {

    interface View : BaseMvpView {
        fun closeView()
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun onButtonDeleteClick(imageNames: List<String>)
        abstract fun onButtonCancelClick()
    }
}