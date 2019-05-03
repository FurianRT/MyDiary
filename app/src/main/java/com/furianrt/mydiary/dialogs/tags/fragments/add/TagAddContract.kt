package com.furianrt.mydiary.dialogs.tags.fragments.add

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface TagAddContract {

    interface View : BaseView {
        fun closeView()
        fun showErrorEmptyTagName()
        fun showErrorExistingTagName()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onButtonAddClick(noteId: String, name: String)
        abstract fun onButtonCloseClick()
    }
}