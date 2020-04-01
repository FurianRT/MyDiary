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

import com.furianrt.mydiary.model.entity.MyNoteWithSpans
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import com.google.common.base.Optional
import io.reactivex.Flowable
import javax.inject.Inject

class GetNotesWithSpansUseCase @Inject constructor(
        private val noteGateway: NoteGateway
) {

    operator fun invoke(noteId: String): Flowable<Optional<MyNoteWithSpans>> =
            noteGateway.getNoteWithSpans(noteId)
}