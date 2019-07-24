package com.furianrt.mydiary.view.screens.main.fragments.premium

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface PremiumContract {

    interface MvpView : BaseMvpView {
        fun close()

    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCloseClick()

    }
}