package com.furianrt.mydiary.note.fragments.edit

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyNote

interface NoteEditFragmentContract {

    interface View : BaseView {

    }

    interface Presenter : BasePresenter<View> {

        fun onStop(note: MyNote, noteTitle: String, noteContent: String)

        fun deleteNote(note: MyNote)
    }
}