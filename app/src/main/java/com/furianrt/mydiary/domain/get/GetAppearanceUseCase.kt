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
import com.google.common.base.Optional
import io.reactivex.Flowable
import javax.inject.Inject

class GetAppearanceUseCase @Inject constructor(
        private val appearanceGateway: AppearanceGateway
) {

    operator fun invoke(noteId: String): Flowable<Optional<MyNoteAppearance>> =
            appearanceGateway.getNoteAppearance(noteId)
                    .map { result ->
                        if (result.isPresent) {
                            result.get().textSize = result.get().textSize ?: appearanceGateway.getTextSize()
                            result.get().textColor = result.get().textColor ?: appearanceGateway.getTextColor()
                            result.get().surfaceTextColor = result.get().surfaceTextColor ?: appearanceGateway.getSurfaceTextColor()
                            result.get().background = result.get().background ?: appearanceGateway.getNoteBackgroundColor()
                            result.get().textBackground = result.get().textBackground ?: appearanceGateway.getNoteTextBackgroundColor()
                        }
                        return@map result
                    }
}