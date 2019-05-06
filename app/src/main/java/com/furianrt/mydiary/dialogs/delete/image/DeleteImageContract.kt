package com.furianrt.mydiary.dialogs.delete.image

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface DeleteImageContract {

    interface View : BaseView {
        fun closeView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonDeleteClick(imageNames: List<String>)
        abstract fun onButtonCancelClick()
    }
}