package com.furianrt.mydiary.dialogs.tags

import javax.inject.Inject

class TagsDialogPresenter @Inject constructor() : TagsDialogContract.Presenter() {

    private lateinit var mNoteId: String

    override fun init(noteId: String) {
        mNoteId = noteId
    }

    override fun attachView(view: TagsDialogContract.MvpView) {
        super.attachView(view)
        view.showTagListView(mNoteId)
    }
}