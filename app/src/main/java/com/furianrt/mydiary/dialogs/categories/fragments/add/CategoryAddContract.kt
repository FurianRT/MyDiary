package com.furianrt.mydiary.dialogs.categories.fragments.add

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface CategoryAddContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun showErrorEmptyName()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonDoneClick(categoryName: String, categoryColor: Int)
        abstract fun onButtonCancelClick()
    }
}