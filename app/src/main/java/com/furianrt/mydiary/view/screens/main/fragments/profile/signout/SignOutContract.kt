package com.furianrt.mydiary.view.screens.main.fragments.profile.signout

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface SignOutContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun returnToMenuView()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonSignOutClick()
        abstract fun onButtonSignOutCancelClick()
    }
}