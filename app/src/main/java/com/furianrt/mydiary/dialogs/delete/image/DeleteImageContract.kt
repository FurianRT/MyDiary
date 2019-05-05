package com.furianrt.mydiary.dialogs.delete.image

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyImage

interface DeleteImageContract {

    interface View : BaseView {
        fun closeView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonDeleteClick(images: List<MyImage>)
        abstract fun onButtonCancelClick()
    }
}