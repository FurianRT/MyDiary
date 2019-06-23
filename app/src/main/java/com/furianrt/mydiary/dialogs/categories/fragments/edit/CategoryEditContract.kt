package com.furianrt.mydiary.dialogs.categories.fragments.edit

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryEditContract {

    interface MvpView : BaseMvpView {
        fun close()
        fun showCategory(category: MyCategory)
        fun showErrorEmptyName()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonDoneClick(category: MyCategory, categoryName: String, categoryColor: Int)
        abstract fun onButtonCancelClick()
    }
}