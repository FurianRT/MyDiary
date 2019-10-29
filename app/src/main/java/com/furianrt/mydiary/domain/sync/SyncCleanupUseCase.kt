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

import com.furianrt.mydiary.model.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.model.repository.category.CategoryRepository
import com.furianrt.mydiary.model.repository.forecast.ForecastRepository
import com.furianrt.mydiary.model.repository.image.ImageRepository
import com.furianrt.mydiary.model.repository.location.LocationRepository
import com.furianrt.mydiary.model.repository.note.NoteRepository
import com.furianrt.mydiary.model.repository.span.SpanRepository
import com.furianrt.mydiary.model.repository.tag.TagRepository
import io.reactivex.Completable
import javax.inject.Inject

class SyncCleanupUseCase @Inject constructor(
        private val noteRepository: NoteRepository,
        private val appearanceRepository: AppearanceRepository,
        private val categoryRepository: CategoryRepository,
        private val tagRepository: TagRepository,
        private val imageRepository: ImageRepository,
        private val locationRepository: LocationRepository,
        private val forecastRepository: ForecastRepository,
        private val spanRepository: SpanRepository
) {

    class SyncCleanupException : Throwable()

    fun invoke(): Completable =
            noteRepository.cleanupNotes()
                    .andThen(appearanceRepository.cleanupAppearances())
                    .andThen(categoryRepository.cleanupCategories())
                    .andThen(tagRepository.cleanupNoteTags())
                    .andThen(tagRepository.cleanupTags())
                    .andThen(imageRepository.cleanupImages())
                    .andThen(locationRepository.cleanupNoteLocations())
                    .andThen(locationRepository.cleanupLocations())
                    .andThen(forecastRepository.cleanupForecasts())
                    .andThen(spanRepository.cleanupTextSpans())
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncCleanupException())
                    }
}