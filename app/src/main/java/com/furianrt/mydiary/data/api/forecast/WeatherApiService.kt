package com.furianrt.mydiary.data.api.forecast

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    fun getForecast(@Query("lat") lat: Double,
                    @Query("lon") lon: Double): Single<Forecast?>
}