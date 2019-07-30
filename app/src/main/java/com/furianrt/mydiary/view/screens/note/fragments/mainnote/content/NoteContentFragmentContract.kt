/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.note.fragments.mainnote.content

import com.furianrt.mydiary.view.base.mvp.BaseMvpView
import com.furianrt.mydiary.view.base.mvp.BaseMvpPresenter

interface NoteContentFragmentContract {

    interface MvpView : BaseMvpView {
        fun showNoteEditViewForTitle(touchPosition: Int)
        fun showNoteEditViewForContent(touchPosition: Int)
        fun showNoteEditViewForTitleEnd()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun onTouchPositionChange(touchPosition: Int)
        abstract fun onTitleClick()
        abstract fun onContentClick()
    }
}