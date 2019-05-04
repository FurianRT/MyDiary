package com.furianrt.mydiary.dialogs.categories.fragments.list

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryListContract {

    interface View : BaseView {
        fun showCategories(categories: List<MyCategory>)
        fun showViewAddCategory()
        fun showEditView(category: MyCategory)
        fun close()
        fun showDeleteCategoryView(category: MyCategory)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onViewStart()
        abstract fun onButtonAddCategoryClick()
        abstract fun onButtonDeleteCategoryClick(category: MyCategory)
        abstract fun onButtonEditCategoryClick(category: MyCategory)
        abstract fun onCategoryClick(category: MyCategory, noteId: String)
        abstract fun onButtonNoCategoryClick(noteId: String)
        abstract fun onButtonCloseClick()
    }
}