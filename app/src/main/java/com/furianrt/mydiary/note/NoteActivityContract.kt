package com.furianrt.mydiary.note

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyNoteWithProp

interface NoteActivityContract {

    interface View : BaseView {
        fun showNotes(notes: List<MyNoteWithProp>)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun loadNotes()
        abstract fun loadNote(noteId: String)
    }
}