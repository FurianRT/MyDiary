/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.forecast

import com.furianrt.mydiary.model.entity.MyForecast
import com.furianrt.mydiary.model.source.api.forecast.WeatherApiService
import com.furianrt.mydiary.model.source.auth.AuthSource
import com.furianrt.mydiary.model.source.cloud.CloudSource
import com.furianrt.mydiary.model.source.database.dao.ForecastDao
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ForecastGatewayImp @Inject constructor(
        private val forecastDao: ForecastDao,
        private val prefs: PreferencesSource,
        private val weatherApi: WeatherApiService,
        private val cloud: CloudSource,
        private val auth: AuthSource,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : ForecastGateway {

    override fun insertForecast(forecasts: List<MyForecast>): Completable =
            forecastDao.insert(forecasts)
                    .subscribeOn(scheduler.io())

    override fun insertForecast(forecast: MyForecast): Completable =
            forecastDao.insert(forecast)
                    .subscribeOn(scheduler.io())

    override fun updateForecastsSync(forecasts: List<MyForecast>): Completable =
            forecastDao.update(forecasts)
                    .subscribeOn(scheduler.io())

    override fun deleteForecast(noteId: String): Completable =
            forecastDao.delete(noteId)
                    .subscribeOn(scheduler.io())

    override fun cleanupForecasts(): Completable =
            forecastDao.cleanup()
                    .subscribeOn(scheduler.io())

    override fun getDeletedForecasts(): Flowable<List<MyForecast>> =
            forecastDao.getDeletedForecasts()
                    .subscribeOn(scheduler.io())

    override fun loadForecast(noteId: String, lat: Double, lon: Double): Single<MyForecast> =
            weatherApi.getForecast(lat, lon)
                    .map {
                        MyForecast(
                                noteId,
                                it.main?.temp!!,
                                ForecastGateway.BASE_WEATHER_IMAGE_URL + it.weather!![0].icon!! + ".png"
                        )
                    }
                    .subscribeOn(scheduler.io())

    override fun getAllDbForecasts(): Single<List<MyForecast>> =
            forecastDao.getAllForecasts()
                    .subscribeOn(scheduler.io())

    override fun saveForecastsInCloud(forecasts: List<MyForecast>): Completable =
            cloud.saveForecasts(forecasts, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun deleteForecastsFromCloud(forecasts: List<MyForecast>): Completable =
            cloud.deleteForecasts(forecasts, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getAllForecastsFromCloud(): Single<List<MyForecast>> =
            cloud.getAllForecasts(auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getWeatherUnits(): Int = prefs.getWeatherUnits()

    override fun isWeatherEnabled(): Boolean = prefs.isWeatherEnabled()
}