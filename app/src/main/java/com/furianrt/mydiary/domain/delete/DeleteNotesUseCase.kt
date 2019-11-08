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

import com.furianrt.mydiary.model.gateway.appearance.AppearanceGateway
import com.furianrt.mydiary.model.gateway.forecast.ForecastGateway
import com.furianrt.mydiary.model.gateway.image.ImageGateway
import com.furianrt.mydiary.model.gateway.location.LocationGateway
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import com.furianrt.mydiary.model.gateway.span.SpanGateway
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class DeleteNotesUseCase @Inject constructor(
        private val noteGateway: NoteGateway,
        private val locationGateway: LocationGateway,
        private val appearanceGateway: AppearanceGateway,
        private val imageGateway: ImageGateway,
        private val forecastGateway: ForecastGateway,
        private val tagGateway: TagGateway,
        private val spanGateway: SpanGateway
) {

    fun invoke(notesIds: List<String>): Completable =
            Observable.fromIterable(notesIds)
                    .flatMapCompletable { deleteNoteWithDependencies(it) }

    private fun deleteNoteWithDependencies(noteId: String): Completable =
            locationGateway.getLocationsForNote(noteId)
                    .first(emptyList())
                    .flatMapCompletable { locations -> locationGateway.deleteLocations(locations.map { it.id }) }
                    .andThen(appearanceGateway.deleteAppearance(noteId))
                    .andThen(tagGateway.deleteNoteTags(noteId))
                    .andThen(imageGateway.getImagesForNote(noteId))
                    .first(emptyList())
                    .flatMapCompletable { images -> imageGateway.deleteImage(images.map { it.name }) }
                    .andThen(forecastGateway.deleteForecast(noteId))
                    .andThen(spanGateway.deleteTextSpan(noteId))
                    .andThen(noteGateway.deleteNote(noteId))
}