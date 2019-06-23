package com.furianrt.mydiary.screens.main.fragments.imagesettings

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface ImageSettingsContract {

    interface MvpView : BaseMvpView {
        fun close()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCloseClick()
    }
}