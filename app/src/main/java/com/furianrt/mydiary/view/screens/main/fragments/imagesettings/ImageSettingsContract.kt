package com.furianrt.mydiary.view.screens.main.fragments.imagesettings

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface ImageSettingsContract {

    interface MvpView : BaseMvpView {
        fun close()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonCloseClick()
    }
}