package com.furianrt.mydiary.screens.note

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteAppearance
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class NoteActivityPresenter @Inject constructor(
        private val dataManager: DataManager
) : NoteActivityContract.Presenter() {

    override fun loadNotes() {
        addDisposable(dataManager.getAllNotesWithProp()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->
                    if (notes.isEmpty()) {
                        view?.closeView()
                    } else {
                        view?.showNotes(if (dataManager.isSortDesc()) {
                            notes.sortedByDescending { it.note.time }
                        } else {
                            notes.sortedBy { it.note.time }
                        })
                    }
                })
    }

    override fun loadNote(noteId: String) {
        val newNote = MyNote(noteId)
        val noteAppearance = MyNoteAppearance(newNote.id)
        addDisposable(dataManager.findNote(noteId)
                .switchIfEmpty(dataManager.insertNote(newNote)
                        .andThen(dataManager.insertAppearance(noteAppearance))
                        .toSingleDefault(newNote))
                .flatMapPublisher { dataManager.getAllNotesWithProp() }
                .map { notes -> notes.filter { it.note.id == noteId } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->
                    if (notes.isEmpty()) {
                        view?.closeView()
                    } else {
                        view?.showNotes(notes)
                    }
                })
    }
}