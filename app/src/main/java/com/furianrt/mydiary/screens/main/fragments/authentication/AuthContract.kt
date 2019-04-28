package com.furianrt.mydiary.screens.main.fragments.authentication

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface AuthContract {

    interface View : BaseView {
        fun closeSheet()
        fun showRegistrationView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCloseClick()
        abstract fun onButtonCreateAccountClick()
    }
}