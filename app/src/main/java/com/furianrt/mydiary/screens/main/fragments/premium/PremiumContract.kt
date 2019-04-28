package com.furianrt.mydiary.screens.main.fragments.premium

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface PremiumContract {

    interface View : BaseView {
        fun close()

    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCloseClick()

    }
}