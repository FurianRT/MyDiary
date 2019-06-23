package com.furianrt.mydiary.dialogs.categories.fragments.delete

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryDeleteContract {

    interface MvpView : BaseMvpView {
        fun closeView()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonDeleteClick(category: MyCategory)
        abstract fun onButtonCancelClick()
    }
}