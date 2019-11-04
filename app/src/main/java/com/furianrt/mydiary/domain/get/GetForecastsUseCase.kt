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

import com.furianrt.mydiary.model.entity.MyForecast
import com.furianrt.mydiary.model.gateway.forecast.ForecastGateway
import io.reactivex.Maybe
import javax.inject.Inject

class GetForecastsUseCase @Inject constructor(
        private val forecastGateway: ForecastGateway
) {

    fun invoke(noteId: String): Maybe<MyForecast> {
        return if (forecastGateway.isWeatherEnabled()) {
            forecastGateway.getAllDbForecasts()
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