package com.furianrt.mydiary.note.fragments.content

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyNote

interface NoteContentFragmentContract {

    interface View : BaseView {

        fun showNote(note: MyNote)
    }

    interface Presenter : BasePresenter<View> {

        fun deleteNote(note: MyNote)
    }
}