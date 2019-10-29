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

import com.furianrt.mydiary.model.repository.forecast.ForecastRepository
import javax.inject.Inject

class IsForecastEnabledUseCase @Inject constructor(
        private val forecastRepository: ForecastRepository
) {

    fun invoke(): Boolean = forecastRepository.isWeatherEnabled()
}