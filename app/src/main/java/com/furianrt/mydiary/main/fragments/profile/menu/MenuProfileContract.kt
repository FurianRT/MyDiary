package com.furianrt.mydiary.main.fragments.profile.menu

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface MenuProfileContract {

    interface View : BaseView {
        fun showSignOutView()
        fun showPasswordView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonSignOutClick()
        abstract fun onButtonChangePasswordClick()
    }
}