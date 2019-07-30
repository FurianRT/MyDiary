/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.repository.forecast

import com.furianrt.mydiary.data.api.forecast.WeatherApiService
import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.ForecastDao
import com.furianrt.mydiary.data.model.MyForecast
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class ForecastRepositoryImp @Inject constructor(
        private val forecastDao: ForecastDao,
        private val prefs: PreferencesHelper,
        private val weatherApi: WeatherApiService,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : ForecastRepository {

    override fun insertForecast(forecasts: List<MyForecast>): Completable =
            forecastDao.insert(forecasts)
                    .subscribeOn(rxScheduler)

    override fun insertForecast(forecast: MyForecast): Completable =
            forecastDao.insert(forecast)
                    .subscribeOn(rxScheduler)

    override fun updateForecastsSync(forecasts: List<MyForecast>): Completable =
            forecastDao.update(forecasts)
                    .subscribeOn(rxScheduler)

    override fun deleteForecast(noteId: String): Completable =
            forecastDao.delete(noteId)
                    .subscribeOn(rxScheduler)

    override fun cleanupForecasts(): Completable =
            forecastDao.cleanup()
                    .subscribeOn(rxScheduler)

    override fun getDeletedForecasts(): Flowable<List<MyForecast>> =
            forecastDao.getDeletedForecasts()
                    .subscribeOn(rxScheduler)

    override fun loadForecast(noteId: String, lat: Double, lon: Double): Single<MyForecast> =
            weatherApi.getForecast(lat, lon)
                    .map {
                        MyForecast(
                                noteId,
                                it.main.temp,
                                ForecastRepository.BASE_WEATHER_IMAGE_URL + it.weather[0].icon + ".png"
                        )
                    }
                    .subscribeOn(rxScheduler)

    override fun getAllDbForecasts(): Single<List<MyForecast>> =
            forecastDao.getAllForecasts()
                    .subscribeOn(rxScheduler)

    override fun saveForecastsInCloud(forecasts: List<MyForecast>): Completable =
            cloud.saveForecasts(forecasts, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteForecastsFromCloud(forecasts: List<MyForecast>): Completable =
            cloud.deleteForecasts(forecasts, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllForecastsFromCloud(): Single<List<MyForecast>> =
            cloud.getAllForecasts(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getWeatherUnits(): Int = prefs.getWeatherUnits()

    override fun isWeatherEnabled(): Boolean = prefs.isWeatherEnabled()
}