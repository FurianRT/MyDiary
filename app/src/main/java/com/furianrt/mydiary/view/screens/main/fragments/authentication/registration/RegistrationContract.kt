/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.authentication.registration

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface RegistrationContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun showErrorNetworkConnection()
        fun showErrorPassword()
        fun showErrorEmailFormat()
        fun showMessageCorrectEmail()
        fun showErrorEmailExists()
        fun clearEmailMessages()
        fun showLoadingEmail()
        fun hideLoadingEmail()
        fun showLoading()
        fun hideLoading()
        fun showErrorShortPassword()
        fun showErrorEmptyPassword()
        fun showErrorEmptyPasswordRepeat()
        fun showErrorEmptyEmail()
        fun showPrivacyView(email: String, password: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCancelClick()
        abstract fun onButtonSignUpClick(email: String, password: String, passwordRepeat: String)
        abstract fun onEmailFocusChange(email: String, hasFocus: Boolean)
    }
}