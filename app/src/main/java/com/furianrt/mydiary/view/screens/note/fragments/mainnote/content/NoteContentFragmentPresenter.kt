package com.furianrt.mydiary.view.screens.note.fragments.mainnote.content

import javax.inject.Inject

class NoteContentFragmentPresenter @Inject constructor() : NoteContentFragmentContract.Presenter() {

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