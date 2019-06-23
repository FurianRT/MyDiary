package com.furianrt.mydiary.dialogs.categories.fragments.list

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryListContract {

    interface MvpView : BaseMvpView {
        fun showCategories(categories: List<MyCategory>)
        fun showViewAddCategory()
        fun showEditView(category: MyCategory)
        fun close()
        fun showDeleteCategoryView(category: MyCategory)
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onViewStart()
        abstract fun onButtonAddCategoryClick()
        abstract fun onButtonDeleteCategoryClick(category: MyCategory)
        abstract fun onButtonEditCategoryClick(category: MyCategory)
        abstract fun onCategoryClick(category: MyCategory, noteIds: List<String>)
        abstract fun onButtonNoCategoryClick(noteIds: List<String>)
        abstract fun onButtonCloseClick()
    }
}