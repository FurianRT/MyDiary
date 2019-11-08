/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.reset

import com.furianrt.mydiary.model.gateway.appearance.AppearanceGateway
import io.reactivex.Completable
import javax.inject.Inject

class ResetNotesAppearanceSettingsUseCase @Inject constructor(
        private val appearanceGateway: AppearanceGateway
) {

    fun invoke(): Completable = Completable.fromAction {
        appearanceGateway.setNoteBackgroundColor(AppearanceGateway.DEFAULT_NOTE_BACKGROUND_COLOR)
        appearanceGateway.setNoteTextBackgroundColor(AppearanceGateway.DEFAULT_NOTE_TEXT_BACKGROUND_COLOR)
        appearanceGateway.setTextSize(AppearanceGateway.DEFAULT_NOTE_TEXT_SIZE)
        appearanceGateway.setTextColor(AppearanceGateway.DEFAULT_NOTE_TEXT_COLOR)
        appearanceGateway.setSurfaceTextColor(AppearanceGateway.DEFAULT_NOTE_TEXT_COLOR)
    }
}