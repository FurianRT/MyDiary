package com.furianrt.mydiary.main

import com.furianrt.mydiary.BasePresenter
import com.furianrt.mydiary.BaseView
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.main.listadapter.MainListItem

interface MainActivityContract {

    interface View : BaseView {

        fun showAdded()

        fun showDeleted()

        fun showNotes(notes: List<MainListItem>?)

        fun openNotePager(position: Int)
    }

    interface Presenter : BasePresenter<View> {

        fun addNote(note: MyNote)

        fun deleteNote(note: MyNote)

        fun loadNotes()

        fun onMainListItemClick(note: MyNote)
    }
}