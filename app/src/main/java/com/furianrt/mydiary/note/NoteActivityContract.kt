package com.furianrt.mydiary.note

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyNote

interface NoteActivityContract {

    interface View : BaseView {

        fun showNotes(notes: List<MyNote>)

    }

    interface Presenter : BasePresenter<View> {

        fun addNote(note: MyNote)

        fun deleteNote(note: MyNote)

        fun loadNotes(mode: Mode)
    }
}