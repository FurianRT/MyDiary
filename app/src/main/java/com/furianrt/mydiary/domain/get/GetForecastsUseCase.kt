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

import com.furianrt.mydiary.data.entity.MyForecast
import com.furianrt.mydiary.data.repository.forecast.ForecastRepository
import io.reactivex.Maybe
import javax.inject.Inject

class GetForecastsUseCase @Inject constructor(
        private val forecastRepository: ForecastRepository
) {

    fun invoke(noteId: String): Maybe<MyForecast> {
        return if (forecastRepository.isWeatherEnabled()) {
            forecastRepository.getAllDbForecasts()
                    .map { forecasts -> forecasts.filter { it.noteId == noteId } }
                    .flatMapMaybe { forecasts ->
                        if (forecasts.isNotEmpty()) {
                            Maybe.just(forecasts.first())
                        } else {
                            Maybe.empty()
                        }
                    }
        } else {
            Maybe.empty()
        }
    }
}