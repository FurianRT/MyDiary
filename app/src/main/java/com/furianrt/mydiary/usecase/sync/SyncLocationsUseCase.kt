package com.furianrt.mydiary.usecase.sync

import com.furianrt.mydiary.data.repository.location.LocationRepository
import io.reactivex.Completable
import javax.inject.Inject

class SyncLocationsUseCase @Inject constructor(
        private val locationRepository: LocationRepository
) {

    class SyncLocationsException : Throwable()
    class SyncNoteLocationsException : Throwable()

    fun invoke(email: String): Completable =
            Completable.concat(listOf(syncLocations(email), syncNoteLocations(email)))

    private fun syncLocations(email: String): Completable =
            locationRepository.getAllDbLocations()
                    .first(emptyList())
                    .map { locations -> locations.filter { !it.isSync(email) } }
                    .map { locations -> locations.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { locations ->
                        Completable.concat(listOf(
                                locationRepository.saveLocationsInCloud(locations),
                                locationRepository.updateLocationsSync(locations)
                        ))
                    }
                    .andThen(locationRepository.getDeletedLocations().first(emptyList()))
                    .flatMapCompletable { locationRepository.deleteLocationsFromCloud(it) }
                    .andThen(locationRepository.getAllLocationsFromCloud())
                    .flatMapCompletable { locationRepository.insertLocation(it) }
                    .onErrorResumeNext { Completable.error(SyncLocationsException()) }

    private fun syncNoteLocations(email: String): Completable =
            locationRepository.getAllNoteLocations()
                    .first(emptyList())
                    .map { noteLocations -> noteLocations.filter { !it.isSync(email) } }
                    .map { noteLocations -> noteLocations.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { noteLocations ->
                        Completable.concat(listOf(
                                locationRepository.saveNoteLocationsInCloud(noteLocations),
                                locationRepository.updateNoteLocationsSync(noteLocations)
                        ))
                    }
                    .andThen(locationRepository.getDeletedNoteLocations().first(emptyList()))
                    .flatMapCompletable { locationRepository.deleteNoteLocationsFromCloud(it) }
                    .andThen(locationRepository.getAllNoteLocationsFromCloud())
                    .flatMapCompletable { locationRepository.insertNoteLocation(it) }
                    .onErrorResumeNext { Completable.error(SyncNoteLocationsException()) }

}