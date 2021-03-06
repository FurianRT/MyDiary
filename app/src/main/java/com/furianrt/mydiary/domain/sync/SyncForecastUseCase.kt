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

import com.furianrt.mydiary.model.gateway.forecast.ForecastGateway
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SyncForecastUseCase @Inject constructor(
        private val forecastGateway: ForecastGateway
) {

    class SyncForecastsException(message: String?, cause: Throwable?) : Throwable(message, cause)

    operator fun invoke(email: String): Completable =
            forecastGateway.getAllDbForecasts()
                    .map { forecasts -> forecasts.filter { !it.isSync(email) } }
                    .map { forecasts -> forecasts.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { forecasts ->
                        Completable.concat(listOf(
                                forecastGateway.saveForecastsInCloud(forecasts),
                                forecastGateway.updateForecastsSync(forecasts)
                        ))
                    }
                    .andThen(forecastGateway.getDeletedForecasts().firstOrError())
                    .flatMapCompletable { forecastGateway.deleteForecastsFromCloud(it).onErrorComplete() }
                    .andThen(forecastGateway.getAllForecastsFromCloud())
                    .flatMapCompletable { forecastGateway.insertForecast(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncForecastsException(error.message, error))
                    }
}