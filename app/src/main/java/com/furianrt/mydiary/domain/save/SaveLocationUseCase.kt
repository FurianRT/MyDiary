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
import com.furianrt.mydiary.model.gateway.location.LocationGateway
import io.reactivex.Completable
import javax.inject.Inject

class SaveLocationUseCase @Inject constructor(
        private val locationGateway: LocationGateway
) {

    operator fun invoke(noteId: String, location: MyLocation): Completable =
            locationGateway.insertLocation(location)
                    .andThen(locationGateway.insertNoteLocation(NoteLocation(noteId, location.id)))
}