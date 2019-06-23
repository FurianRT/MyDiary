package com.furianrt.mydiary.screens.main.fragments.premium

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface PremiumContract {

    interface MvpView : BaseMvpView {
        fun close()

    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCloseClick()

    }
}