package com.furianrt.mydiary.main.fragments.profile.signout

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface SignOutContract {

    interface View : BaseView {
        fun close()
        fun returnToMenuView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonSignOutClick()
        abstract fun onButtonSignOutCancelClick()
    }
}