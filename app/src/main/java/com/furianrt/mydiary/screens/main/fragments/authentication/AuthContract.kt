package com.furianrt.mydiary.screens.main.fragments.authentication

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface AuthContract {

    interface MvpView : BaseMvpView {
        fun closeSheet()
        fun showRegistrationView()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCloseClick()
        abstract fun onButtonCreateAccountClick()
    }
}