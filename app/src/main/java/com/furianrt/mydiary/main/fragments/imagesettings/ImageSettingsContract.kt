package com.furianrt.mydiary.main.fragments.imagesettings

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface ImageSettingsContract {

    interface View : BaseView {

        fun close()
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onButtonCloseClick()
    }
}