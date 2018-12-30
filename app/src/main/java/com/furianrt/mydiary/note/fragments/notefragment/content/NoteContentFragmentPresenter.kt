package com.furianrt.mydiary.note.fragments.notefragment.content

import com.furianrt.mydiary.data.DataManager

class NoteContentFragmentPresenter(private val mDataManager: DataManager)
    : NoteContentFragmentContract.Presenter {

    private var mView: NoteContentFragmentContract.View? = null

    override fun attachView(view: NoteContentFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }
}