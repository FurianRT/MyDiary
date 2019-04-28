package com.furianrt.mydiary.screens.main.fragments.authentication.login

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface LoginContract {

    interface View : BaseView {
        fun isNetworkAvailable(): Boolean
        fun showErrorEmptyEmail()
        fun showErrorNetworkConnection()
        fun showErrorEmptyPassword()
        fun showLoginSuccess()
        fun showErrorWrongCredential()
        fun showLoading()
        fun hideLoading()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonForgotClick()
        abstract fun onButtonSignInClick(email: String, password: String)

    }
}