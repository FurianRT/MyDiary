package com.furianrt.mydiary.dialogs.tags

import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.base.BaseView

interface TagsDialogContract {

    interface View : BaseView {
        fun showTagListView(noteId: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onViewCreated(noteId: String)
    }
}