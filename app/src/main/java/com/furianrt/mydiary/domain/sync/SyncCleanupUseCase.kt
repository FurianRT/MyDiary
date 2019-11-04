/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.sync

import com.furianrt.mydiary.model.gateway.appearance.AppearanceGateway
import com.furianrt.mydiary.model.gateway.category.CategoryGateway
import com.furianrt.mydiary.model.gateway.forecast.ForecastGateway
import com.furianrt.mydiary.model.gateway.image.ImageGateway
import com.furianrt.mydiary.model.gateway.location.LocationGateway
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import com.furianrt.mydiary.model.gateway.span.SpanGateway
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import io.reactivex.Completable
import javax.inject.Inject

class SyncCleanupUseCase @Inject constructor(
        private val noteGateway: NoteGateway,
        private val appearanceGateway: AppearanceGateway,
        private val categoryGateway: CategoryGateway,
        private val tagGateway: TagGateway,
        private val imageGateway: ImageGateway,
        private val locationGateway: LocationGateway,
        private val forecastGateway: ForecastGateway,
        private val spanGateway: SpanGateway
) {

    class SyncCleanupException : Throwable()

    fun invoke(): Completable =
            noteGateway.cleanupNotes()
                    .andThen(appearanceGateway.cleanupAppearances())
                    .andThen(categoryGateway.cleanupCategories())
                    .andThen(tagGateway.cleanupNoteTags())
                    .andThen(tagGateway.cleanupTags())
                    .andThen(imageGateway.cleanupImages())
                    .andThen(locationGateway.cleanupNoteLocations())
                    .andThen(locationGateway.cleanupLocations())
                    .andThen(forecastGateway.cleanupForecasts())
                    .andThen(spanGateway.cleanupTextSpans())
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncCleanupException())
                    }
}