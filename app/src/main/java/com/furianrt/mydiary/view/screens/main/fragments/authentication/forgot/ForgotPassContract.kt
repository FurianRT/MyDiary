package com.furianrt.mydiary.view.screens.main.fragments.authentication.forgot

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface ForgotPassContract {

    interface MvpView : BaseMvpView {
        fun showErrorEmptyEmail()
        fun showErrorEmailFormat()
        fun close()
        fun showLoading()
        fun hideLoading()
        fun showEmailSent()
        fun showErrorNetworkConnection()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCancelClick()
        abstract fun onButtonSendClick(email: String)
    }
}