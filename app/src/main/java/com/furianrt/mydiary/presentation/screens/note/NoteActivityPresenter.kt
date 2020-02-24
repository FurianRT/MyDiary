/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note

import com.furianrt.mydiary.domain.save.SaveNoteIfNotExistUseCase
import com.furianrt.mydiary.domain.get.GetNotesUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class NoteActivityPresenter @Inject constructor(
        private val getNotesUseCase: GetNotesUseCase,
        private val saveNoteIfNotExistUseCase: SaveNoteIfNotExistUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : NoteActivityContract.Presenter() {

    private lateinit var mNoteId: String
    private var mIsNewNote: Boolean = true

    override fun init(noteId: String, newNote: Boolean) {
        mNoteId = noteId
        mIsNewNote = newNote

    }

    override fun attachView(view: NoteActivityContract.View) {
        super.attachView(view)
        check(::mNoteId.isInitialized) { "Need to call init before attaching view" }
        if (mIsNewNote) {
            loadNote(mNoteId)
        } else {
            loadNotes()
        }
    }

    private fun loadNotes() {
        addDisposable(getNotesUseCase()
                .observeOn(scheduler.ui())
                .subscribe { notes ->
                    if (notes.isEmpty()) {
                        view?.closeView()
                    } else {
                        view?.showNotes(notes.map { it.id })
                    }
                })
    }

    private fun loadNote(noteId: String) {
        addDisposable(saveNoteIfNotExistUseCase(noteId)
                .andThen(getNotesUseCase(noteId))
                .observeOn(scheduler.ui())
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