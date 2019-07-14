package com.furianrt.mydiary.view.dialogs.tags

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface TagsDialogContract {

    interface MvpView : BaseMvpView {
        fun showTagListView(noteId: String)
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun init(noteId: String)
    }
}