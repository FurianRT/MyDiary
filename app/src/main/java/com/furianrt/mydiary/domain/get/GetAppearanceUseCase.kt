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
import com.furianrt.mydiary.model.gateway.appearance.AppearanceGateway
import io.reactivex.Flowable
import javax.inject.Inject

class GetAppearanceUseCase @Inject constructor(
        private val appearanceGateway: AppearanceGateway
) {

    fun invoke(noteId: String): Flowable<MyNoteAppearance> =
            appearanceGateway.getNoteAppearance(noteId)
                    .map { appearance ->
                        appearance.textSize =
                                appearance.textSize ?: appearanceGateway.getTextSize()
                        appearance.textColor =
                                appearance.textColor ?: appearanceGateway.getTextColor()
                        appearance.surfaceTextColor =
                                appearance.surfaceTextColor ?: appearanceGateway.getSurfaceTextColor()
                        appearance.background =
                                appearance.background ?: appearanceGateway.getNoteBackgroundColor()
                        appearance.textBackground =
                                appearance.textBackground ?: appearanceGateway.getNoteTextBackgroundColor()
                        return@map appearance
                    }
}