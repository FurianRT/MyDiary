package com.furianrt.mydiary.screens.main.fragments.imagesettings

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface ImageSettingsContract {

    interface View : BaseView {

        fun close()
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onButtonCloseClick()
    }
}