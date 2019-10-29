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

import com.furianrt.mydiary.model.entity.MyLocation
import com.furianrt.mydiary.model.repository.location.LocationRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
        private val locationRepository: LocationRepository
) {

    fun invoke(): Flowable<List<MyLocation>> = locationRepository.getAllDbLocations()

    fun invoke(noteId: String): Flowable<List<MyLocation>> =
            locationRepository.getLocationsForNote(noteId)
}