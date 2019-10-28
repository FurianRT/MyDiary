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
import com.furianrt.mydiary.model.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.model.repository.note.NoteRepository
import io.reactivex.Completable
import javax.inject.Inject

class SaveNotesUseCase @Inject constructor(
        private val noteRepository: NoteRepository,
        private val appearanceRepository: AppearanceRepository
) {

    fun invoke(note: MyNote): Completable =
            noteRepository.insertNote(note)
                    .andThen(appearanceRepository.insertAppearance(MyNoteAppearance(note.id)))
}