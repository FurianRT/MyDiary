package com.furianrt.mydiary.screens.main.fragments.authentication.forgot

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface ForgotPassContract {

    interface View : BaseView {
        fun showErrorEmptyEmail()
        fun showErrorEmailFormat()
        fun close()
        fun showLoading()
        fun hideLoading()
        fun showEmailSent()
        fun showErrorNetworkConnection()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCancelClick()
        abstract fun onButtonSendClick(email: String)
    }
}