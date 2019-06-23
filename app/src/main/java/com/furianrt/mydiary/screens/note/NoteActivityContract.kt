package com.furianrt.mydiary.screens.note

import com.furianrt.mydiary.base.BaseMvpView
import com.furianrt.mydiary.base.BasePresenter
import com.furianrt.mydiary.data.model.MyNoteWithProp

interface NoteActivityContract {

    interface MvpView : BaseMvpView {
        fun showNotes(notes: List<MyNoteWithProp>)
        fun closeView()
    }

    abstract class Presenter : BasePresenter<MvpView>() {
        abstract fun loadNotes()
        abstract fun loadNote(noteId: String)
    }
}