package com.furianrt.mydiary.dialogs.categories.fragments.add

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryAddContract {

    interface View : BaseView {
        fun close()
        fun showErrorEmptyName()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonDoneClick(category: MyCategory, categoryName: String, categoryColor: Int)
        abstract fun onButtonCancelClick()
    }
}