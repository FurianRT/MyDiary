package com.furianrt.mydiary.screens.note.fragments.mainnote.content

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter

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