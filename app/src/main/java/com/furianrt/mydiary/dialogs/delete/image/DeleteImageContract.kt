package com.furianrt.mydiary.dialogs.delete.image

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface DeleteImageContract {

    interface MvpView : BaseMvpView {
        fun closeView()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonDeleteClick(imageNames: List<String>)
        abstract fun onButtonCancelClick()
    }
}