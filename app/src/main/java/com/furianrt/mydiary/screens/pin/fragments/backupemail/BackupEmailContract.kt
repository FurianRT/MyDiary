package com.furianrt.mydiary.screens.pin.fragments.backupemail

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface BackupEmailContract {

    interface MvpView : BaseMvpView {
        fun showEmailIsCorrect(email: String)
        fun showErrorEmailFormat()
        fun showEmail(email: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonDoneClick(email: String)
        abstract fun onViewCreated(email: String, firstLaunch: Boolean)

    }
}