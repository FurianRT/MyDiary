package com.furianrt.mydiary.dialogs.categories.fragments.delete

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter
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