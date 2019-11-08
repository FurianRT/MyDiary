/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.sync

import com.furianrt.mydiary.model.gateway.note.NoteGateway
import io.reactivex.Completable
import javax.inject.Inject

class SyncNotesUseCase @Inject constructor(
        private val noteGateway: NoteGateway
) {

    class SyncNotesException : Throwable()

    fun invoke(email: String): Completable =
            noteGateway.getAllNotes()
                    .first(emptyList())
                    .map { notes -> notes.filter { !it.isSync(email) } }
                    .map { notes -> notes.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { notes ->
                        Completable.concat(listOf(
                                noteGateway.saveNotesInCloud(notes),
                                noteGateway.updateNotesSync(notes)
                        ))
                    }
                    .andThen(noteGateway.getDeletedNotes().first(emptyList()))
                    .flatMapCompletable { noteGateway.deleteNotesFromCloud(it) }
                    .andThen(noteGateway.getAllNotesFromCloud())
                    .flatMapCompletable { noteGateway.insertNote(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncNotesException())
                    }
}