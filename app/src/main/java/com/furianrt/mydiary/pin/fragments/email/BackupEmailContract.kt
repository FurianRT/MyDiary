package com.furianrt.mydiary.pin.fragments.email

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface BackupEmailContract {

    interface View : BaseView {
        fun showEmailIsCorrect(email: String)
        fun showErrorEmptyEmail()
        fun showErrorEmailFormat()
        fun showEmail(email: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonDoneClick(email: String)
        abstract fun onViewCreated(email: String, firstLaunch: Boolean)

    }
}