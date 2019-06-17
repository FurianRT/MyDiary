package com.furianrt.mydiary.dialogs.rate

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface RateDialogContract {
    interface View : BaseView {
        fun sendEmailToSupport()
        fun openAppPage()
        fun close()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonRateClick(rating: Int)
        abstract fun onButtonLaterClick()
        abstract fun onButtonNeverClick()
    }
}