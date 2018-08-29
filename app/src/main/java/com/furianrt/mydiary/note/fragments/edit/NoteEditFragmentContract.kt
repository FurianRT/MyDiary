package com.furianrt.mydiary.note.fragments.edit

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyNote

interface NoteEditFragmentContract {

    interface View : BaseView {

    }

    interface Presenter : BasePresenter<View> {

        fun addOrUpdateNote(note: MyNote)

        fun deleteNote(note: MyNote)
    }
}