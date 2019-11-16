/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.content

import javax.inject.Inject

class NoteContentPresenter @Inject constructor() : NoteContentContract.Presenter() {

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