/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.sync

import com.furianrt.mydiary.data.repository.forecast.ForecastRepository
import io.reactivex.Completable
import javax.inject.Inject

class SyncForecastUseCase @Inject constructor(
        private val forecastRepository: ForecastRepository
) {

    class SyncForecastsException : Throwable()

    fun invoke(email: String): Completable =
            forecastRepository.getAllDbForecasts()
                    .map { forecasts -> forecasts.filter { !it.isSync(email) } }
                    .map { forecasts -> forecasts.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { forecasts ->
                        Completable.concat(listOf(
                                forecastRepository.saveForecastsInCloud(forecasts),
                                forecastRepository.updateForecastsSync(forecasts)
                        ))
                    }
                    .andThen(forecastRepository.getDeletedForecasts().first(emptyList()))
                    .flatMapCompletable { forecastRepository.deleteForecastsFromCloud(it) }
                    .andThen(forecastRepository.getAllForecastsFromCloud())
                    .flatMapCompletable { forecastRepository.insertForecast(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncForecastsException())
                    }
}