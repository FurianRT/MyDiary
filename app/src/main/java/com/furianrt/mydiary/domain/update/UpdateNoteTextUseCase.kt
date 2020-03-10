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

import android.annotation.SuppressLint
import com.furianrt.mydiary.di.application.component.AppScope
import com.furianrt.mydiary.model.entity.NoteData
import javax.inject.Inject

@AppScope
class UpdateNoteTextUseCase @Inject constructor(
        private val updateNoteSpansUseCase: UpdateNoteSpansUseCase,
        private val updateNoteUseCase: UpdateNoteUseCase
) {

    @SuppressLint("CheckResult")
    operator fun invoke(noteData: NoteData) {
        updateNoteUseCase(noteData.noteId, noteData.title, noteData.content)
                .andThen(updateNoteSpansUseCase(noteData.noteId, noteData.textSpans))
                .subscribe()
    }
}