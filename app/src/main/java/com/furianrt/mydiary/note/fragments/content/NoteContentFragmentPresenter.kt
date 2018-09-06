package com.furianrt.mydiary.note.fragments.content

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote

class NoteContentFragmentPresenter(private val mDataManager: DataManager)
    : NoteContentFragmentContract.Presenter {

    private var mView: NoteContentFragmentContract.View? = null

    override fun findNote(note: MyNote) {
        mDataManager.findNote(note.id)
                .subscribe { mView?.showNote(note) }
    }

    override fun deleteNote(note: MyNote) {
        mDataManager.deleteNote(note)
                .subscribe()
    }

    override fun attachView(view: NoteContentFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }
}