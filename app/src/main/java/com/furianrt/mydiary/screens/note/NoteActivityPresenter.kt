package com.furianrt.mydiary.screens.note

import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.domain.get.GetNotesUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class NoteActivityPresenter @Inject constructor(
        private val getNotes: GetNotesUseCase
) : NoteActivityContract.Presenter() {

    private lateinit var mNoteId: String
    private var mIsNewNote: Boolean = true

    override fun init(noteId: String, newNote: Boolean) {
        mNoteId = noteId
        mIsNewNote = newNote

    }

    override fun attachView(view: NoteActivityContract.MvpView) {
        super.attachView(view)
        if (!::mNoteId.isInitialized) {
            throw IllegalStateException("Need to call init before attaching view")
        }

        if (mIsNewNote) {
            loadNote(mNoteId)
        } else {
            loadNotes()
        }
    }

    private fun loadNotes() {
        addDisposable(getNotes.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { notes ->
                    if (notes.isEmpty()) {
                        view?.closeView()
                    } else {
                        view?.showNotes(notes.map { it.id })
                    }
                })
    }

    private fun loadNote(noteId: String) {
        val newNote = MyNote(noteId)
        val noteAppearance = MyNoteAppearance(newNote.id)
        addDisposable(dataManager.findNote(noteId)
                .switchIfEmpty(dataManager.insertNote(newNote)
                        .andThen(dataManager.insertAppearance(noteAppearance))
                        .toSingleDefault(newNote))
                .flatMapPublisher { dataManager.getNoteAsList(noteId) }
                .map { note -> note.map { it.id } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { Id ->
                    if (Id.isEmpty()) {
                        view?.closeView()
                    } else {
                        view?.showNotes(Id)
                    }
                })
    }
}