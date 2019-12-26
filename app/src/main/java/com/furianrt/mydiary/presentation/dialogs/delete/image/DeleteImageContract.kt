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

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface DeleteImageContract {

    interface View : BaseView {
        fun closeView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonDeleteClick(imageNames: List<String>)
        abstract fun onButtonCancelClick()
    }
}