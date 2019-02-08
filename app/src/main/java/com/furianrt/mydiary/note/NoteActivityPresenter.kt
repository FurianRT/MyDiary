package com.furianrt.mydiary.note

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.data.model.MyNoteWithProp

class NoteActivityPresenter(private val mDataManager: DataManager) : NoteActivityContract.Presenter() {


    override fun loadNotes() {
        addDisposable(mDataManager.getAllNotesWithProp()
                .subscribe { view?.showNotes(it) })
    }

    override fun loadNote(noteId: String) {
        val tempNote = MyNote(noteId, "", "")
        val noteAppearance = MyNoteAppearance(tempNote.id)
        addDisposable(mDataManager.findNote(noteId)
                .switchIfEmpty(mDataManager.insertNote(tempNote)
                        .andThen(mDataManager.insertAppearance(noteAppearance))
                        .toSingleDefault(tempNote))
                .subscribe { note ->
                    view?.showNotes(listOf(MyNoteWithProp(note, null, null, null)))
                })

    }
}