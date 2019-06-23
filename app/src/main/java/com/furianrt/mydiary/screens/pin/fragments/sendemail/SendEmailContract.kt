package com.furianrt.mydiary.screens.pin.fragments.sendemail

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface SendEmailContract {

    interface MvpView : BaseMvpView {
        fun closeView()
        fun showLoading()
        fun hideLoading()
        fun showDoneView()
        fun showErrorMessageSend()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonSendClick()
        abstract fun onButtonCancelClick()
    }
}