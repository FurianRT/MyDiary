package com.furianrt.mydiary.view.dialogs.categories.fragments.add

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface CategoryAddContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun showErrorEmptyName()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onButtonDoneClick(categoryName: String, categoryColor: Int)
        abstract fun onButtonCancelClick()
    }
}