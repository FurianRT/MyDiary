package com.furianrt.mydiary.view.dialogs.categories.fragments.list

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryListContract {

    interface MvpView : BaseMvpView {
        fun showCategories(categories: List<MyCategory>)
        fun showViewAddCategory()
        fun showEditView(category: MyCategory)
        fun close()
        fun showDeleteCategoryView(category: MyCategory)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onViewStart()
        abstract fun onButtonAddCategoryClick()
        abstract fun onButtonDeleteCategoryClick(category: MyCategory)
        abstract fun onButtonEditCategoryClick(category: MyCategory)
        abstract fun onCategoryClick(category: MyCategory, noteIds: List<String>)
        abstract fun onButtonNoCategoryClick(noteIds: List<String>)
        abstract fun onButtonCloseClick()
    }
}