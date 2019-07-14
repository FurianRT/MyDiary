package com.furianrt.mydiary.usecase.save

import com.furianrt.mydiary.data.model.MyForecast
import com.furianrt.mydiary.data.repository.forecast.ForecastRepository
import com.furianrt.mydiary.usecase.get.GetForecastsUseCase
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