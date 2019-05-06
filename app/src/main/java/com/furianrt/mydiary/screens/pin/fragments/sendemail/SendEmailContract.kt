package com.furianrt.mydiary.screens.pin.fragments.sendemail

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface SendEmailContract {

    interface View : BaseView {
        fun closeView()
        fun showLoading()
        fun hideLoading()
        fun showDoneView()
        fun showErrorMessageSend()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonSendClick()
        abstract fun onButtonCancelClick()
    }
}