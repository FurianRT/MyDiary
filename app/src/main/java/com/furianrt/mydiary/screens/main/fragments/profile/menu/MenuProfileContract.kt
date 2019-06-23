package com.furianrt.mydiary.screens.main.fragments.profile.menu

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface MenuProfileContract {

    interface MvpView : BaseMvpView {
        fun showSignOutView()
        fun showPasswordView()
        fun showAboutView()
        fun disableSignOut(disable: Boolean)
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonSignOutClick()
        abstract fun onButtonChangePasswordClick()
        abstract fun onButtonAboutClick()
    }
}