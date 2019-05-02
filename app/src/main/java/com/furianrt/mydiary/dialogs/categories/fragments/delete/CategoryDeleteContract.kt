package com.furianrt.mydiary.dialogs.categories.fragments.delete

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryDeleteContract {

    interface View : BaseView {
        fun closeView()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonDeleteClick(category: MyCategory)
        abstract fun onButtonCancelClick()
    }
}