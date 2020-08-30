/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.save

import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.furianrt.mydiary.model.gateway.appearance.AppearanceGateway
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SaveNoteIfNotExistUseCase @Inject constructor(
        private val noteGateway: NoteGateway,
        private val appearanceGateway: AppearanceGateway
) {

    operator fun invoke(noteId: String): Completable =
        noteGateway.insertNote(MyNote(noteId))
                .andThen(appearanceGateway.insertAppearance(MyNoteAppearance(noteId)))
}