package com.furianrt.mydiary.screens.main.fragments.profile.menu

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface MenuProfileContract {

    interface View : BaseView {
        fun showSignOutView()
        fun showPasswordView()
        fun showAboutView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonSignOutClick()
        abstract fun onButtonChangePasswordClick()
        abstract fun onButtonAboutClick()
    }
}