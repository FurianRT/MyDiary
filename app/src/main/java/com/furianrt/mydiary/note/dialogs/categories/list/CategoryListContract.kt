package com.furianrt.mydiary.note.dialogs.categories.list

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryListContract {

    interface View : BaseView {

        fun showCategories(categories: List<MyCategory>)

        fun showViewAddCategory()

        fun showEditView(category: MyCategory)

        fun close()
    }

    interface Presenter : BasePresenter<View> {

        fun onViewCreate()

        fun onAddCategoryButtonClick()

        fun onDeleteCategoryButtonClick(category: MyCategory)

        fun onEditCategoryButtonClick(category: MyCategory)

        fun onCategoryClick(category: MyCategory, noteId: String)
    }
}