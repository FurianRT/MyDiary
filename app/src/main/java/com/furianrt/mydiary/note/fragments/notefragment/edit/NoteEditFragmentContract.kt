package com.furianrt.mydiary.note.fragments.notefragment.edit

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyNote

interface NoteEditFragmentContract {

    interface View : BaseView {

        fun closeView()

    }

    abstract class Presenter : BasePresenter<View>() {

        abstract fun onStop(note: MyNote, noteTitle: String, noteContent: String)

        abstract fun deleteNote(note: MyNote)

        abstract fun onDoneButtonClick()
    }
}