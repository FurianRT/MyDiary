package com.furianrt.mydiary.usecase.check

import com.furianrt.mydiary.data.repository.location.LocationRepository
import javax.inject.Inject

class IsLocationEnabledUseCase @Inject constructor(
        private val locationRepository: LocationRepository
) {

    fun invoke(): Boolean = locationRepository.isLocationEnabled()
}