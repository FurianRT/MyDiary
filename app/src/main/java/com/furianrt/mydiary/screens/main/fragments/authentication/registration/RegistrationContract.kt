package com.furianrt.mydiary.screens.main.fragments.authentication.registration

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface RegistrationContract {

    interface View : BaseView {
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

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCancelClick()
        abstract fun onButtonSignUpClick(email: String, password: String, passwordRepeat: String)
        abstract fun onEmailFocusChange(email: String, hasFocus: Boolean)
    }
}