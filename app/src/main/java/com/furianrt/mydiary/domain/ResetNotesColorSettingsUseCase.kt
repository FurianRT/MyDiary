/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain

import com.furianrt.mydiary.data.repository.appearance.AppearanceRepository
import io.reactivex.Completable
import javax.inject.Inject

class ResetNotesAppearanceSettingsUseCase @Inject constructor(
        private val appearanceRepository: AppearanceRepository
) {

    fun invoke(): Completable = Completable.fromAction {
        appearanceRepository.setNoteBackgroundColor(AppearanceRepository.DEFAULT_NOTE_BACKGROUND_COLOR)
        appearanceRepository.setNoteTextBackgroundColor(AppearanceRepository.DEFAULT_NOTE_TEXT_BACKGROUND_COLOR)
        appearanceRepository.setTextSize(AppearanceRepository.DEFAULT_NOTE_TEXT_SIZE)
        appearanceRepository.setTextColor(AppearanceRepository.DEFAULT_NOTE_TEXT_COLOR)
        appearanceRepository.setSurfaceTextColor(AppearanceRepository.DEFAULT_NOTE_TEXT_COLOR)
    }
}