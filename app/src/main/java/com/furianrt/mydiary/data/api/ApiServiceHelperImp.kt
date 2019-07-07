package com.furianrt.mydiary.data.api

import com.furianrt.mydiary.data.api.forecast.WeatherApiService
import com.furianrt.mydiary.data.api.images.Image
import com.furianrt.mydiary.data.api.images.ImageApiService
import com.furianrt.mydiary.data.model.MyForecast
import com.furianrt.mydiary.data.model.MyHeaderImage
import io.reactivex.Single
import org.joda.time.DateTime
import javax.inject.Inject

class ApiServiceHelperImp @Inject constructor(
        private val weatherApi: WeatherApiService,
        private val imageApi: ImageApiService
) : ApiServiceHelper {

    companion object {
        private const val BASE_WEATHER_IMAGE_URL = "http://openweathermap.org/img/w/"
    }

    private fun Image.toMyHeaderImage(): MyHeaderImage =
            MyHeaderImage(id, largeImageURL, DateTime.now().millis)

    override fun getForecast(lat: Double, lon: Double): Single<MyForecast> =
            weatherApi.getForecast(lat, lon)
                    .map {
                        MyForecast(
                                temp = it.main.temp,
                                icon = BASE_WEATHER_IMAGE_URL + it.weather[0].icon + ".png"
                        )
                    }

    override fun getImages(category: String, page: Int, perPage: Int): Single<List<MyHeaderImage>> =
            imageApi.getImages(category, page, perPage)
                    .map { response ->
                        response.images
                                .map { it.toMyHeaderImage() }
                                .sortedByDescending { it.addedTime }
                    }
}