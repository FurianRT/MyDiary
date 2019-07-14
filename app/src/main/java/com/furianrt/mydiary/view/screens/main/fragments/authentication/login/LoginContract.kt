package com.furianrt.mydiary.view.screens.main.fragments.authentication.login

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface LoginContract {

    interface MvpView : BaseMvpView {
        fun isNetworkAvailable(): Boolean
        fun showErrorEmptyEmail()
        fun showErrorNetworkConnection()
        fun showErrorEmptyPassword()
        fun showLoginSuccess()
        fun showErrorWrongCredential()
        fun showLoading()
        fun hideLoading()
        fun showForgotPassView(email: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonForgotClick(email: String)
        abstract fun onButtonSignInClick(email: String, password: String)

    }
}