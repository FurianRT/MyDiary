/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain

import com.furianrt.mydiary.model.gateway.location.LocationGateway
import javax.inject.Inject

class DisableLocationUseCase @Inject constructor(
        private val locationGateway: LocationGateway
) {

    fun invoke() {
        locationGateway.setLocationEnabled(false)
    }
}