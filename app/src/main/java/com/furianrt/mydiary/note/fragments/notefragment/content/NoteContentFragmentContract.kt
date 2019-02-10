package com.furianrt.mydiary.note.fragments.notefragment.content

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView

interface NoteContentFragmentContract {

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