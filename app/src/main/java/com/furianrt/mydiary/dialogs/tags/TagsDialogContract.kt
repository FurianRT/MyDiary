package com.furianrt.mydiary.dialogs.tags

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

interface TagsDialogContract {

    interface MvpView : BaseMvpView {
        fun showTagListView(noteId: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onViewCreated(noteId: String)
    }
}