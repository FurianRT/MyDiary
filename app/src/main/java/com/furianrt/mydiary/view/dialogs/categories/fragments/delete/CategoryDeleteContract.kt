package com.furianrt.mydiary.view.dialogs.categories.fragments.delete

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryDeleteContract {

    interface MvpView : BaseMvpView {
        fun closeView()
        fun showDeleteMessage(name: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(category: MyCategory)
        abstract fun onButtonDeleteClick()
        abstract fun onButtonCancelClick()
    }
}