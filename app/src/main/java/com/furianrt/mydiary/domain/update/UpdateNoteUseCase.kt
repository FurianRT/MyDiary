/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.update

import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
        private val noteGateway: NoteGateway
) {

    operator fun invoke(note: MyNote): Completable = noteGateway.updateNote(note)

    operator fun invoke(noteId: String, moodId: Int): Completable =
            noteGateway.getNote(noteId)
                    .flatMapCompletable { noteGateway.updateNote(it.apply { this.moodId = moodId }) }

    operator fun invoke(noteIds: List<String>, categoryId: String): Completable =
            Observable.fromIterable(noteIds)
                    .flatMapSingle { noteGateway.getNote(it) }
                    .flatMapSingle { note ->
                        noteGateway.updateNote(note.apply { this.categoryId = categoryId })
                                .toSingleDefault(true)
                    }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()

    operator fun invoke(noteId: String, title: String, content: String): Completable =
            noteGateway.getNote(noteId)
                    .flatMapCompletable { note ->
                        if (note.title != title || note.content != content) {
                            noteGateway.updateNoteText(noteId, title, content)
                        } else {
                            Completable.complete()
                        }
                    }
}