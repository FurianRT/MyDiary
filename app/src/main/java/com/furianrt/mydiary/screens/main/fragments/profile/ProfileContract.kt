package com.furianrt.mydiary.screens.main.fragments.profile

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface ProfileContract {

    interface View : BaseView {
        fun close()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCloseClick()
    }
}