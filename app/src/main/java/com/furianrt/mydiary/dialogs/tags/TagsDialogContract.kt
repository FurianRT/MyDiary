package com.furianrt.mydiary.dialogs.tags

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter

interface TagsDialogContract {

    interface MvpView : BaseMvpView {
        fun showTagListView(noteId: String)
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun onViewCreated(noteId: String)
    }
}