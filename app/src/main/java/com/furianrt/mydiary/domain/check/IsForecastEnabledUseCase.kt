package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.data.repository.forecast.ForecastRepository
import javax.inject.Inject

class IsForecastEnabledUseCase @Inject constructor(
        private val forecastRepository: ForecastRepository
) {

    fun invoke(): Boolean = forecastRepository.isWeatherEnabled()
}