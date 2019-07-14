package com.furianrt.mydiary.dialogs.rate

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

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