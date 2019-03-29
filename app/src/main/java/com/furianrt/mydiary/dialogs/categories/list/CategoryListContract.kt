package com.furianrt.mydiary.dialogs.categories.list

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryListContract {

    interface View : BaseView {

        fun showCategories(categories: List<MyCategory>)

        fun showViewAddCategory()

        fun showEditView(category: MyCategory)

        fun close()
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onViewStart()

        abstract fun onAddCategoryButtonClick()

        abstract fun onDeleteCategoryButtonClick(category: MyCategory)

        abstract fun onEditCategoryButtonClick(category: MyCategory)

        abstract fun onCategoryClick(category: MyCategory, noteId: String)
    }
}