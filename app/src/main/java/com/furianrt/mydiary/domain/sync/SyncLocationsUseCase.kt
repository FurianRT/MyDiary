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

import com.furianrt.mydiary.model.gateway.location.LocationGateway
import io.reactivex.Completable
import javax.inject.Inject

class SyncLocationsUseCase @Inject constructor(
        private val locationGateway: LocationGateway
) {

    class SyncLocationsException : Throwable()
    class SyncNoteLocationsException : Throwable()

    operator fun invoke(email: String): Completable =
            Completable.concat(listOf(syncLocations(email), syncNoteLocations(email)))

    private fun syncLocations(email: String): Completable =
            locationGateway.getAllDbLocations()
                    .first(emptyList())
                    .map { locations -> locations.filter { !it.isSync(email) } }
                    .map { locations -> locations.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { locations ->
                        Completable.concat(listOf(
                                locationGateway.saveLocationsInCloud(locations),
                                locationGateway.updateLocationsSync(locations)
                        ))
                    }
                    .andThen(locationGateway.getDeletedLocations().first(emptyList()))
                    .flatMapCompletable { locationGateway.deleteLocationsFromCloud(it) }
                    .andThen(locationGateway.getAllLocationsFromCloud())
                    .flatMapCompletable { locationGateway.insertLocation(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncLocationsException())
                    }

    private fun syncNoteLocations(email: String): Completable =
            locationGateway.getAllNoteLocations()
                    .first(emptyList())
                    .map { noteLocations -> noteLocations.filter { !it.isSync(email) } }
                    .map { noteLocations -> noteLocations.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { noteLocations ->
                        Completable.concat(listOf(
                                locationGateway.saveNoteLocationsInCloud(noteLocations),
                                locationGateway.updateNoteLocationsSync(noteLocations)
                        ))
                    }
                    .andThen(locationGateway.getDeletedNoteLocations().first(emptyList()))
                    .flatMapCompletable { locationGateway.deleteNoteLocationsFromCloud(it) }
                    .andThen(locationGateway.getAllNoteLocationsFromCloud())
                    .flatMapCompletable { locationGateway.insertNoteLocation(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncNoteLocationsException())
                    }

}