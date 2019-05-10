package com.furianrt.mydiary.screens.note.fragments.mainnote.content

import com.furianrt.mydiary.data.DataManager

class NoteContentFragmentPresenter(private val dataManager: DataManager)
    : NoteContentFragmentContract.Presenter() {

    private var mTouchPosition = 0

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