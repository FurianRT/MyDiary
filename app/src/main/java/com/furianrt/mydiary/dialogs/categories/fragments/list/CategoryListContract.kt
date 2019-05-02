package com.furianrt.mydiary.dialogs.categories.fragments.list

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryListContract {

    interface View : BaseView {
        fun showCategories(categories: List<MyCategory>)
        fun showViewAddCategory()
        fun showEditView(categoryId: String)
        fun close()
        fun showDeleteCategoryView(category: MyCategory)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onViewStart()
        abstract fun onAddCategoryButtonClick()
        abstract fun onDeleteCategoryButtonClick(category: MyCategory)
        abstract fun onEditCategoryButtonClick(category: MyCategory)
        abstract fun onCategoryClick(category: MyCategory, noteId: String)
    }
}