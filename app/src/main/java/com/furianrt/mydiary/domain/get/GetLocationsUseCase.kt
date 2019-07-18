package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.repository.location.LocationRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
        private val locationRepository: LocationRepository
) {

    fun invoke(): Flowable<List<MyLocation>> = locationRepository.getAllDbLocations()

    fun invoke(noteId: String): Flowable<List<MyLocation>> =
            locationRepository.getLocationsForNote(noteId)
}