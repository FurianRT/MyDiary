package com.furianrt.mydiary.view.screens.pin.fragments.sendemail

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

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