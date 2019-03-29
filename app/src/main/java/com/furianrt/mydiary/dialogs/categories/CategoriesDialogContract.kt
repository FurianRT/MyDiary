package com.furianrt.mydiary.dialogs.categories

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface CategoriesDialogContract {

    interface View : BaseView {

        fun showViewCategoryList(noteId: String)
    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onViewCreate(noteId: String)
    }
}