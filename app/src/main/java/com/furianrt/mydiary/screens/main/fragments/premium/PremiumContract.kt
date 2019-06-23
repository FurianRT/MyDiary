package com.furianrt.mydiary.screens.main.fragments.premium

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface PremiumContract {

    interface MvpView : BaseMvpView {
        fun close()

    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonCloseClick()

    }
}