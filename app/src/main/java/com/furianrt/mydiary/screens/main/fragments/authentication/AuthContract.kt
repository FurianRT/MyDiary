package com.furianrt.mydiary.screens.main.fragments.authentication

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface AuthContract {

    interface MvpView : BaseMvpView {
        fun closeSheet()
        fun showRegistrationView()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonCloseClick()
        abstract fun onButtonCreateAccountClick()
    }
}