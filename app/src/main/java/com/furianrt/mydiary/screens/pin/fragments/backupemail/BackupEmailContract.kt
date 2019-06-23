package com.furianrt.mydiary.screens.pin.fragments.backupemail

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface BackupEmailContract {

    interface MvpView : BaseMvpView {
        fun showEmailIsCorrect(email: String)
        fun showErrorEmptyEmail()
        fun showErrorEmailFormat()
        fun showEmail(email: String)
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonDoneClick(email: String)
        abstract fun onViewCreated(email: String, firstLaunch: Boolean)

    }
}