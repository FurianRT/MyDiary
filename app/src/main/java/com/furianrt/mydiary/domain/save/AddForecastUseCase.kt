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
import com.furianrt.mydiary.model.repository.forecast.ForecastRepository
import com.furianrt.mydiary.domain.get.GetForecastsUseCase
import io.reactivex.Maybe
import javax.inject.Inject

class AddForecastUseCase @Inject constructor(
        private val forecastRepository: ForecastRepository,
        private val getForecasts: GetForecastsUseCase
) {

    fun invoke(noteId: String, latitude: Double, longitude: Double): Maybe<MyForecast> =
            forecastRepository.loadForecast(noteId, latitude, longitude)
                    .flatMapCompletable { forecastRepository.insertForecast(it) }
                    .andThen(getForecasts.invoke(noteId))
}