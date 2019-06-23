package com.furianrt.mydiary.screens.pin.fragments.sendemail

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface SendEmailContract {

    interface MvpView : BaseMvpView {
        fun closeView()
        fun showLoading()
        fun hideLoading()
        fun showDoneView()
        fun showErrorMessageSend()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonSendClick()
        abstract fun onButtonCancelClick()
    }
}