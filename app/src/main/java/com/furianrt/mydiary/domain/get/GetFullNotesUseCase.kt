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

import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import com.google.common.base.Optional
import io.reactivex.Flowable
import javax.inject.Inject

//Следит почти за всеми таблицами. Использовать только когда дейстительно необходимо!
class GetFullNotesUseCase @Inject constructor(
        private val noteGateway: NoteGateway
) {

    operator fun invoke(): Flowable<List<MyNoteWithProp>> =
            noteGateway.getAllNotesWithProp()
                    .map { notes ->
                        if (noteGateway.isSortDesc()) {
                            notes.sortedByDescending { it.note.time }
                        } else {
                            notes.sortedBy { it.note.time }
                        }
                    }

    operator fun invoke(noteId: String): Flowable<Optional<MyNoteWithProp>> =
            noteGateway.getNoteWithProp(noteId)
}