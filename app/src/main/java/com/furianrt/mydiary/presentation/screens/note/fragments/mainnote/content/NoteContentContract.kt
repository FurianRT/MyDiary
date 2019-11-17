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

import com.furianrt.mydiary.presentation.base.BaseView
import com.furianrt.mydiary.presentation.base.BasePresenter

interface NoteContentContract {

    interface View : BaseView {
        fun showNoteEditViewForTitle(touchPosition: Int)
        fun showNoteEditViewForContent(touchPosition: Int)
        fun showNoteEditViewForTitleEnd()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun onTouchPositionChange(touchPosition: Int)
        abstract fun onTitleClick()
        abstract fun onContentClick()
    }
}