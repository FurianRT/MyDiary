package com.furianrt.mydiary.main.fragments.profile

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface ProfileContract {

    interface View : BaseView {
        fun showSignOut()
        fun close()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonSignOutClick()
        abstract fun onButtonCloseClick()
    }
}