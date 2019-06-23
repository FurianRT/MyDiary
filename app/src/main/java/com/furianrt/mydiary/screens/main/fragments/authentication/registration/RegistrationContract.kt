package com.furianrt.mydiary.screens.main.fragments.authentication.registration

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface RegistrationContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun isNetworkAvailable(): Boolean
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