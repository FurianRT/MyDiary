package com.furianrt.mydiary.main.fragments.profile

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface ProfileContract {

    interface View : BaseView {
        fun close()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonCloseClick()
    }
}