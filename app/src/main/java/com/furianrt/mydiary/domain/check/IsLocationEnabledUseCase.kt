/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.model.repository.location.LocationRepository
import javax.inject.Inject

class IsLocationEnabledUseCase @Inject constructor(
        private val locationRepository: LocationRepository
) {

    fun invoke(): Boolean = locationRepository.isLocationEnabled()
}