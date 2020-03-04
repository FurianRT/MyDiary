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

import com.furianrt.mydiary.model.gateway.location.LocationGateway
import javax.inject.Inject

class IsLocationEnabledUseCase @Inject constructor(
        private val locationGateway: LocationGateway
) {

    operator fun invoke(): Boolean = locationGateway.isLocationEnabled()
}