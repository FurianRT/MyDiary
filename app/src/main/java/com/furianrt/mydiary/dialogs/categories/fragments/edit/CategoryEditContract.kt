package com.furianrt.mydiary.dialogs.categories.fragments.edit

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryEditContract {

    interface View : BaseView {
        fun close()
        fun showCategory(category: MyCategory)
        fun showErrorEmptyName()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonDoneClick(category: MyCategory, categoryName: String, categoryColor: Int)
        abstract fun onButtonCancelClick()
    }
}