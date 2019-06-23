package com.furianrt.mydiary.screens.main.fragments.profile.menu

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface MenuProfileContract {

    interface MvpView : BaseMvpView {
        fun showSignOutView()
        fun showPasswordView()
        fun showAboutView()
        fun disableSignOut(disable: Boolean)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonSignOutClick()
        abstract fun onButtonChangePasswordClick()
        abstract fun onButtonAboutClick()
    }
}