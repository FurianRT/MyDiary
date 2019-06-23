package com.furianrt.mydiary.dialogs.rate

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface RateDialogContract {
    interface MvpView : BaseMvpView {
        fun sendEmailToSupport()
        fun openAppPage()
        fun close()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonRateClick(rating: Int)
        abstract fun onButtonLaterClick()
        abstract fun onButtonNeverClick()
    }
}