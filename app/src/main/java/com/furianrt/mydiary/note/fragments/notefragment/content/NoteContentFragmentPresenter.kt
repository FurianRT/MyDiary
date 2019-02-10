package com.furianrt.mydiary.note.fragments.notefragment.content

import com.furianrt.mydiary.data.DataManager

class NoteContentFragmentPresenter(private val mDataManager: DataManager)
    : NoteContentFragmentContract.Presenter() {

    private var mTouchPosition = 0

    override fun onEditButtonClick() {
        view?.showNoteEditViewForTitleEnd()
    }

    override fun onTouchPositionChange(touchPosition: Int) {
        mTouchPosition = touchPosition
    }

    override fun onTitleClick() {
        view?.showNoteEditViewForTitle(mTouchPosition)
    }

    override fun onContentClick() {
        view?.showNoteEditViewForContent(mTouchPosition)
    }
}