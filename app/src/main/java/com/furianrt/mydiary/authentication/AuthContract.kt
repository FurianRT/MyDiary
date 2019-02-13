package com.furianrt.mydiary.authentication

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

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