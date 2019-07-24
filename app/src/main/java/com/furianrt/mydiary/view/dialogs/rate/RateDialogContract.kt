package com.furianrt.mydiary.view.dialogs.rate

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface RateDialogContract {
    interface MvpView : BaseMvpView {
        fun sendEmailToSupport(supportEmail: String)
        fun openAppPage()
        fun close()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonRateClick(rating: Int)
        abstract fun onButtonLaterClick()
        abstract fun onButtonNeverClick()
    }
}