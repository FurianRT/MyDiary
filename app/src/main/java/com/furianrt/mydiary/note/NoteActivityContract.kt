package com.furianrt.mydiary.note

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyNoteWithProp

interface NoteActivityContract {

    interface View : BaseView {

        fun showNotes(notes: List<MyNoteWithProp>)

    }

    interface Presenter : BasePresenter<View> {

        fun loadNotes()

        fun loadNote(noteId: String)
    }
}