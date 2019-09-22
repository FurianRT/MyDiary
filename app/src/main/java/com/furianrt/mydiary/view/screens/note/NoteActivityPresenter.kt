/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.note

import com.furianrt.mydiary.domain.save.SaveNoteIfNotExistUseCase
import com.furianrt.mydiary.domain.get.GetNotesUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class NoteActivityPresenter @Inject constructor(
        private val getNotes: GetNotesUseCase,
        private val saveNoteIfNotExist: SaveNoteIfNotExistUseCase
) : NoteActivityContract.Presenter() {

    private lateinit var mNoteId: String
    private var mIsNewNote: Boolean = true

    override fun init(noteId: String, newNote: Boolean) {
        mNoteId = noteId
        mIsNewNote = newNote

    }

    override fun attachView(view: NoteActivityContract.MvpView) {
        super.attachView(view)
        check(::mNoteId.isInitialized) { "Need to call init before attaching view" }
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
        addDisposable(saveNoteIfNotExist.invoke(noteId)
                .andThen(getNotes.invoke(noteId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { note ->
                    if (note.isPresent) {
                        view?.showNotes(listOf(note.get().id))
                    } else {
                        view?.closeView()
                    }
                }
        )
    }
}