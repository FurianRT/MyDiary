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

import com.furianrt.mydiary.data.entity.MyNoteAppearance
import com.furianrt.mydiary.data.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.data.repository.note.NoteRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateAppearanceUseCase @Inject constructor(
        private val appearanceRepository: AppearanceRepository,
        private val noteRepository: NoteRepository
) {

    fun invoke(appearance: MyNoteAppearance): Completable =
            appearanceRepository.updateAppearance(appearance.apply { syncWith.clear() })
                    .andThen(noteRepository.getNote(appearance.appearanceId))
                    .map { it.apply { it.syncWith.clear() } }
                    .flatMapCompletable { noteRepository.updateNote(it) }
}