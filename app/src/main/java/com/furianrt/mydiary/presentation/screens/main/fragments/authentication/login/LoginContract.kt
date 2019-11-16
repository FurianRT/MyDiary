/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.authentication.login

import com.furianrt.mydiary.presentation.base.mvp.BaseMvpView
import com.furianrt.mydiary.presentation.base.mvp.BaseMvpPresenter

interface LoginContract {

    interface View : BaseMvpView {
        fun showErrorEmptyEmail()
        fun showErrorNetworkConnection()
        fun showErrorEmptyPassword()
        fun showLoginSuccess()
        fun showErrorWrongCredential()
        fun showLoading()
        fun hideLoading()
        fun showForgotPassView(email: String)
    }

    abstract class Presenter : BaseMvpPresenter<View>() {
        abstract fun onButtonForgotClick(email: String)
        abstract fun onButtonSignInClick(email: String, password: String)

    }
}