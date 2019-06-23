package com.furianrt.mydiary.screens.main.fragments.profile.signout

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface SignOutContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun returnToMenuView()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonSignOutClick()
        abstract fun onButtonSignOutCancelClick()
    }
}