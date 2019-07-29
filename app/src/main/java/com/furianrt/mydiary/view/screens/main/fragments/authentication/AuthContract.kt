/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.authentication

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface AuthContract {

    interface MvpView : BaseMvpView {
        fun closeSheet()
        fun showRegistrationView()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCloseClick()
        abstract fun onButtonCreateAccountClick()
    }
}