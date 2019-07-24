package com.furianrt.mydiary.domain.delete

import com.furianrt.mydiary.data.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.data.repository.forecast.ForecastRepository
import com.furianrt.mydiary.data.repository.image.ImageRepository
import com.furianrt.mydiary.data.repository.location.LocationRepository
import com.furianrt.mydiary.data.repository.note.NoteRepository
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
        private val tagRepository: TagRepository
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
                    .andThen(noteRepository.deleteNote(noteId))
}