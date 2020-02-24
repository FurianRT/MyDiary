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

import com.furianrt.mydiary.model.entity.MyLocation
import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import io.reactivex.Maybe
import javax.inject.Inject

class FindLocationUseCase @Inject constructor(
        private val deviceGateway: DeviceGateway
) {

    operator fun invoke(): Maybe<MyLocation> =
            if (deviceGateway.isLocationAvailable()) {
                deviceGateway.findLocation()
            } else {
                Maybe.empty()
            }
}