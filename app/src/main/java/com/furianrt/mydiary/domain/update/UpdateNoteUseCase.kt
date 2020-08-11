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

import com.furianrt.mydiary.domain.get.GetNotesUseCase
import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
        private val getNotesUseCase: GetNotesUseCase,
        private val noteGateway: NoteGateway
) {

    operator fun invoke(note: MyNote): Completable = noteGateway.updateNote(note)

    operator fun invoke(noteId: String, moodId: Int): Completable =
            getNotesUseCase(noteId)
                    .firstOrError()
                    .flatMapCompletable { result ->
                        if (result.isPresent) {
                            this(result.get().apply { this.moodId = moodId })
                        } else {
                            Completable.complete()
                        }
                    }

    operator fun invoke(noteIds: List<String>, categoryId: String): Completable =
            Observable.fromIterable(noteIds)
                    .flatMapSingle { getNotesUseCase(it).firstOrError() }
                    .flatMapSingle { result ->
                        if (result.isPresent) {
                            this(result.get().apply { this.categoryId = categoryId })
                                    .toSingleDefault(true)
                        } else {
                            Single.just(false)
                        }
                    }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()

    operator fun invoke(noteId: String, title: String, content: String): Completable =
            getNotesUseCase(noteId)
                    .firstOrError()
                    .flatMapCompletable { note ->
                        if (note.isPresent && (note.get().title != title || note.get().content != content)) {
                            noteGateway.updateNoteText(noteId, title, content)
                        } else {
                            Completable.complete()
                        }
                    }
}