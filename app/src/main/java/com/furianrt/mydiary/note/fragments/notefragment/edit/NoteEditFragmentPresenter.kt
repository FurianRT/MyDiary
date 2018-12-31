package com.furianrt.mydiary.note.fragments.notefragment.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote

class NoteEditFragmentPresenter(private val mDataManager: DataManager)
    : NoteEditFragmentContract.Presenter() {

    private var mView: NoteEditFragmentContract.View? = null

    override fun onStop(note: MyNote, noteTitle: String, noteContent: String) {
        //if (!note.title.isEmpty() || !note.content.isEmpty()) {
        if (note.title != noteTitle || note.content != noteContent) {
            note.title = noteTitle
            note.content = noteContent

            addDisposable(mDataManager.updateNoteText(note.id, noteTitle, noteContent)
                    .subscribe())

        }

        // }
    }

    override fun deleteNote(note: MyNote) {
    }

    override fun onDoneButtonClick() {
        mView?.closeView()
    }

    override fun attachView(view: NoteEditFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }
}