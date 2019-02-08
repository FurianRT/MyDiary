package com.furianrt.mydiary.note.fragments.notefragment.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote

class NoteEditFragmentPresenter(private val mDataManager: DataManager)
    : NoteEditFragmentContract.Presenter() {

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
        view?.closeView()
    }
}