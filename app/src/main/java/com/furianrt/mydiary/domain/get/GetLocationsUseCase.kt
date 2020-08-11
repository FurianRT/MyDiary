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
import com.furianrt.mydiary.model.gateway.location.LocationGateway
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
        private val locationGateway: LocationGateway
) {

    operator fun invoke(): Flowable<List<MyLocation>> = locationGateway.getAllDbLocations()

    operator fun invoke(noteId: String): Flowable<List<MyLocation>> =
            locationGateway.getLocationsForNote(noteId)
}