package com.furianrt.mydiary.dialogs.tags.fragments.add

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface TagAddContract {

    interface MvpView : BaseMvpView {
        fun closeView()
        fun showErrorEmptyTagName()
        fun showErrorExistingTagName()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onButtonAddClick(noteId: String, name: String)
        abstract fun onButtonCloseClick()
    }
}