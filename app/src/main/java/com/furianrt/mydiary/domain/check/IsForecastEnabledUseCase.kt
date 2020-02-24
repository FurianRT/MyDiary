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

import com.furianrt.mydiary.model.gateway.forecast.ForecastGateway
import javax.inject.Inject

class IsForecastEnabledUseCase @Inject constructor(
        private val forecastGateway: ForecastGateway
) {

    operator fun invoke(): Boolean = forecastGateway.isWeatherEnabled()
}