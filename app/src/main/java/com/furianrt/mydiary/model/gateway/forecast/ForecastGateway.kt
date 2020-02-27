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
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface ForecastGateway {

    companion object {
        const val WEATHER_UNITS_CELSIUS = 0
        const val WEATHER_UNITS_FAHRENHEIT = 1
        const val BASE_WEATHER_IMAGE_URL = "https://openweathermap.org/img/w/"
    }

    fun insertForecast(forecasts: List<MyForecast>): Completable
    fun insertForecast(forecast: MyForecast): Completable
    fun updateForecastsSync(forecasts: List<MyForecast>): Completable
    fun deleteForecast(noteId: String): Completable
    fun deleteForecastsFromCloud(forecasts: List<MyForecast>): Completable
    fun cleanupForecasts(): Completable
    fun getDeletedForecasts(): Flowable<List<MyForecast>>
    fun loadForecast(noteId: String, lat: Double, lon: Double): Single<MyForecast>
    fun getAllForecastsFromCloud(): Single<List<MyForecast>>
    fun getAllDbForecasts(): Single<List<MyForecast>>
    fun saveForecastsInCloud(forecasts: List<MyForecast>): Completable
    fun getWeatherUnits(): Int
    fun isWeatherEnabled(): Boolean
    fun setWeatherEnabled(enabled: Boolean)
}