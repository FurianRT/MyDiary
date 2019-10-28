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

import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.furianrt.mydiary.model.repository.appearance.AppearanceRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetAppearanceUseCase @Inject constructor(
        private val appearanceRepository: AppearanceRepository
) {

    fun invoke(noteId: String): Flowable<MyNoteAppearance> =
            appearanceRepository.getNoteAppearance(noteId)
                    .map { appearance ->
                        appearance.textSize =
                                appearance.textSize ?: appearanceRepository.getTextSize()
                        appearance.textColor =
                                appearance.textColor ?: appearanceRepository.getTextColor()
                        appearance.surfaceTextColor =
                                appearance.surfaceTextColor ?: appearanceRepository.getSurfaceTextColor()
                        appearance.background =
                                appearance.background ?: appearanceRepository.getNoteBackgroundColor()
                        appearance.textBackground =
                                appearance.textBackground ?: appearanceRepository.getNoteTextBackgroundColor()
                        return@map appearance
                    }
}