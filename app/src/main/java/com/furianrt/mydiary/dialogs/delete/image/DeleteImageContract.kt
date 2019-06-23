package com.furianrt.mydiary.dialogs.delete.image

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface DeleteImageContract {

    interface MvpView : BaseMvpView {
        fun closeView()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonDeleteClick(imageNames: List<String>)
        abstract fun onButtonCancelClick()
    }
}