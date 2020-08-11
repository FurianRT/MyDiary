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

import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
        private val noteGateway: NoteGateway
) {

    operator fun invoke(): Flowable<List<MyNote>> =
            noteGateway.getAllNotes()
                    .map { notes ->
                        if (noteGateway.isSortDesc()) {
                            notes.sortedByDescending { it.time }
                        } else {
                            notes.sortedBy { it.time }
                        }
                    }

    operator fun invoke(noteId: String): Flowable<Optional<MyNote>> = noteGateway.getNote(noteId)
}