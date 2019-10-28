/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.save

import com.furianrt.mydiary.model.entity.MyLocation
import com.furianrt.mydiary.model.entity.NoteLocation
import com.furianrt.mydiary.model.repository.location.LocationRepository
import io.reactivex.Completable
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(
        private val locationRepository: LocationRepository
) {

    fun invoke(noteId: String, location: MyLocation): Completable =
            locationRepository.insertLocation(location)
                    .andThen(locationRepository.insertNoteLocation(NoteLocation(noteId, location.id)))
}