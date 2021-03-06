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

import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.furianrt.mydiary.model.gateway.appearance.AppearanceGateway
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class UpdateAppearanceUseCase @Inject constructor(
        private val appearanceGateway: AppearanceGateway,
        private val noteGateway: NoteGateway
) {

    operator fun invoke(appearance: MyNoteAppearance): Completable =
            appearanceGateway.updateAppearance(appearance.apply { syncWith.clear() })
                    .andThen(noteGateway.getNote(appearance.appearanceId).firstOrError())
                    .flatMapCompletable { result ->
                        if (result.isPresent) {
                            noteGateway.updateNote(result.get().apply { syncWith.clear() })
                        } else {
                            Completable.complete()
                        }
                    }
}