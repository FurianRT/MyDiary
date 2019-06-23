package com.furianrt.mydiary.screens.note

import com.furianrt.mydiary.base.mvp.BaseMvpView
import com.furianrt.mydiary.base.mvp.BaseMvpPresenter
import com.furianrt.mydiary.data.model.MyNoteWithProp

interface NoteActivityContract {

    interface MvpView : BaseMvpView {
        fun showNotes(notes: List<MyNoteWithProp>)
        fun closeView()
    }

    abstract class Presenter : BaseMvpPresenter<MvpView>() {
        abstract fun loadNotes()
        abstract fun loadNote(noteId: String)
    }
}