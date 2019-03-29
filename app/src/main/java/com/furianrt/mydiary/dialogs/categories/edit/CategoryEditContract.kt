package com.furianrt.mydiary.dialogs.categories.edit

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryEditContract {

    interface View : BaseView {

        fun close()

        fun showCategory(category: MyCategory)
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onViewCreate()

        abstract fun onButtonDoneClick(category: MyCategory)

        abstract fun loadCategory(categoryId: String)
    }
}