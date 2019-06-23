package com.furianrt.mydiary.dialogs.categories.fragments.add

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

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