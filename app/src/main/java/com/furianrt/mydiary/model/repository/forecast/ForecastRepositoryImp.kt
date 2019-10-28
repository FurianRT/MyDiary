/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.repository.forecast

import com.furianrt.mydiary.model.entity.MyForecast
import com.furianrt.mydiary.model.source.api.forecast.WeatherApiService
import com.furianrt.mydiary.model.source.auth.AuthHelper
import com.furianrt.mydiary.model.source.cloud.CloudHelper
import com.furianrt.mydiary.model.source.database.ForecastDao
import com.furianrt.mydiary.model.source.preferences.PreferencesHelper
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class ForecastRepositoryImp @Inject constructor(
        private val forecastDao: ForecastDao,
        private val prefs: PreferencesHelper,
        private val weatherApi: WeatherApiService,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : ForecastRepository {

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
                                it.main.temp,
                                ForecastRepository.BASE_WEATHER_IMAGE_URL + it.weather[0].icon + ".png"
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