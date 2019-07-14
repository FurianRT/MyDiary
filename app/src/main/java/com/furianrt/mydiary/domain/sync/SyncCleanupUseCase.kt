package com.furianrt.mydiary.domain.sync

import com.furianrt.mydiary.data.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.data.repository.category.CategoryRepository
import com.furianrt.mydiary.data.repository.forecast.ForecastRepository
import com.furianrt.mydiary.data.repository.image.ImageRepository
import com.furianrt.mydiary.data.repository.location.LocationRepository
import com.furianrt.mydiary.data.repository.note.NoteRepository
import com.furianrt.mydiary.data.repository.tag.TagRepository
import io.reactivex.Completable
import javax.inject.Inject

class SyncCleanupUseCase @Inject constructor(
        private val noteRepository: NoteRepository,
        private val appearanceRepository: AppearanceRepository,
        private val categoryRepository: CategoryRepository,
        private val tagRepository: TagRepository,
        private val imageRepository: ImageRepository,
        private val locationRepository: LocationRepository,
        private val forecastRepository: ForecastRepository
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
                    .onErrorResumeNext { Completable.error(SyncCleanupException()) }

}