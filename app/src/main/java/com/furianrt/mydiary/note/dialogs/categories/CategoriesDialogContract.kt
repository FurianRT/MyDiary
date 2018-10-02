package com.furianrt.mydiary.note.dialogs.categories

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface CategoriesDialogContract {

    interface View : BaseView {

        fun showViewCategoryList(noteId: String)
    }

    interface Presenter : BasePresenter<View> {

        fun onViewCreate(noteId: String)
    }
}