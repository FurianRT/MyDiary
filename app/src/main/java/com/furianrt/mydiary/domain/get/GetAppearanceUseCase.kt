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
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class GetAppearanceUseCase @Inject constructor(
        private val appearanceGateway: AppearanceGateway
) {

    operator fun invoke(noteId: String): Flowable<Optional<MyNoteAppearance>> {
        val defaultTextSize = appearanceGateway.getTextSize()
        val defaultTextColor = appearanceGateway.getTextColor()
        val defaultSurfaceTextColor = appearanceGateway.getSurfaceTextColor()
        val defaultNoteBackgroundColor = appearanceGateway.getNoteBackgroundColor()
        val defaultNoteTextBackgroundColor = appearanceGateway.getNoteTextBackgroundColor()
        return appearanceGateway.getNoteAppearance(noteId)
                .map { result ->
                    if (result.isPresent) {
                        result.get().textSize = result.get().textSize ?: defaultTextSize
                        result.get().textColor = result.get().textColor ?: defaultTextColor
                        result.get().surfaceTextColor = result.get().surfaceTextColor ?: defaultSurfaceTextColor
                        result.get().background = result.get().background ?: defaultNoteBackgroundColor
                        result.get().textBackground = result.get().textBackground ?: defaultNoteTextBackgroundColor
                    }
                    return@map result
                }
    }

    operator fun invoke(): Flowable<List<MyNoteAppearance>> {
        val defaultTextSize = appearanceGateway.getTextSize()
        val defaultTextColor = appearanceGateway.getTextColor()
        val defaultSurfaceTextColor = appearanceGateway.getSurfaceTextColor()
        val defaultNoteBackgroundColor = appearanceGateway.getNoteBackgroundColor()
        val defaultNoteTextBackgroundColor = appearanceGateway.getNoteTextBackgroundColor()
        return appearanceGateway.getAllNoteAppearances()
                .map { appearances ->
                    appearances.map { appearance ->
                        appearance.apply {
                            textSize = appearance.textSize ?: defaultTextSize
                            textColor = appearance.textColor ?: defaultTextColor
                            surfaceTextColor = appearance.surfaceTextColor ?: defaultSurfaceTextColor
                            background = appearance.background ?: defaultNoteBackgroundColor
                            textBackground = appearance.textBackground ?: defaultNoteTextBackgroundColor
                        }
                    }
                }
    }
}