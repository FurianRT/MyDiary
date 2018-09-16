package com.furianrt.mydiary.note

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteWithProp

interface NoteActivityContract {

    interface View : BaseView {

        fun showNotes(notes: List<MyNoteWithProp>)

    }

    interface Presenter : BasePresenter<View> {

        fun addNote(note: MyNote)

        fun deleteNote(note: MyNote)

        fun loadNotes(mode: Mode)
    }
}