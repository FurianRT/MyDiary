package com.furianrt.mydiary.screens.main.fragments.imagesettings

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface ImageSettingsContract {

    interface MvpView : BaseMvpView {
        fun close()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonCloseClick()
    }
}