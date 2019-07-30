/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.repository.forecast.ForecastRepository
import javax.inject.Inject

class GetWeatherUnitsUseCase @Inject constructor(
        private val forecastRepository: ForecastRepository
) {

    companion object {
        const val UNITS_CELSIUS = 0
        const val UNITS_FAHRENHEIT = 1
    }

    fun invoke(): Int =
            when (forecastRepository.getWeatherUnits()) {
                ForecastRepository.WEATHER_UNITS_CELSIUS -> UNITS_CELSIUS
                ForecastRepository.WEATHER_UNITS_FAHRENHEIT -> UNITS_FAHRENHEIT
                else -> throw IllegalStateException()
            }
}