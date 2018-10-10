package com.furianrt.mydiary.note.dialogs.categories.edit

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyCategory

interface CategoryEditContract {

    interface View : BaseView {

        fun close()

        fun showCategory(category: MyCategory)
    }

    interface Presenter : BasePresenter<View> {

        fun onViewCreate()

        fun onButtonDoneClick(category: MyCategory)

        fun loadCategory(categoryId: Long)
    }
}