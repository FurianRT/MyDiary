/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.repository.note.NoteRepository
import com.google.common.base.Optional
import io.reactivex.Flowable
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
        private val noteRepository: NoteRepository
) {

    fun invoke(): Flowable<List<MyNote>> =
            noteRepository.getAllNotes()
                    .map { notes ->
                        if (noteRepository.isSortDesc()) {
                            notes.sortedByDescending { it.time }
                        } else {
                            notes.sortedBy { it.time }
                        }
                    }

    fun invoke(noteId: String): Flowable<Optional<MyNote>> =
            noteRepository.getNoteAsList(noteId)
                    .map { note -> Optional.fromNullable(note.find { it.id == noteId }) }
}