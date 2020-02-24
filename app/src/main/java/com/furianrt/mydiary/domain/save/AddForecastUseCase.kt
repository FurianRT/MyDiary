/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.save

import com.furianrt.mydiary.model.entity.MyForecast
import com.furianrt.mydiary.model.gateway.forecast.ForecastGateway
import com.furianrt.mydiary.domain.get.GetForecastsUseCase
import io.reactivex.Maybe
import javax.inject.Inject

class AddForecastUseCase @Inject constructor(
        private val forecastGateway: ForecastGateway,
        private val getForecastsUseCase: GetForecastsUseCase
) {

    operator fun invoke(noteId: String, latitude: Double, longitude: Double): Maybe<MyForecast> =
            forecastGateway.loadForecast(noteId, latitude, longitude)
                    .flatMapCompletable { forecastGateway.insertForecast(it) }
                    .andThen(getForecastsUseCase(noteId))
}