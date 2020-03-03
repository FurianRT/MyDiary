/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface AuthContract {

    interface View : BaseView {
        fun close()
        fun showRegistrationView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCloseClick()
        abstract fun onButtonCreateAccountClick()
    }
}