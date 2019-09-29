/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.delete

import com.furianrt.mydiary.data.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.data.repository.forecast.ForecastRepository
import com.furianrt.mydiary.data.repository.image.ImageRepository
import com.furianrt.mydiary.data.repository.location.LocationRepository
import com.furianrt.mydiary.data.repository.note.NoteRepository
import com.furianrt.mydiary.data.repository.span.SpanRepository
import com.furianrt.mydiary.data.repository.tag.TagRepository
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class DeleteNotesUseCase @Inject constructor(
        private val noteRepository: NoteRepository,
        private val locationRepository: LocationRepository,
        private val appearanceRepository: AppearanceRepository,
        private val imageRepository: ImageRepository,
        private val forecastRepository: ForecastRepository,
        private val tagRepository: TagRepository,
        private val spanRepository: SpanRepository
) {

    fun invoke(notesIds: List<String>): Completable =
            Observable.fromIterable(notesIds)
                    .flatMapCompletable { deleteNoteWithDependencies(it) }

    private fun deleteNoteWithDependencies(noteId: String): Completable =
            locationRepository.getLocationsForNote(noteId)
                    .first(emptyList())
                    .flatMapCompletable { locations -> locationRepository.deleteLocations(locations.map { it.id }) }
                    .andThen(appearanceRepository.deleteAppearance(noteId))
                    .andThen(tagRepository.deleteNoteTags(noteId))
                    .andThen(imageRepository.getImagesForNote(noteId))
                    .first(emptyList())
                    .flatMapCompletable { images -> imageRepository.deleteImage(images.map { it.name }) }
                    .andThen(forecastRepository.deleteForecast(noteId))
                    .andThen(spanRepository.deleteTextSpan(noteId))
                    .andThen(noteRepository.deleteNote(noteId))
}