package com.furianrt.mydiary.data.api

import com.furianrt.mydiary.data.model.MyForecast
import com.furianrt.mydiary.data.model.MyHeaderImage
import io.reactivex.Single

interface ApiServiceHelper {
    fun getForecast(lat: Double, lon: Double): Single<MyForecast>
    fun getImages(category: String, page: Int = 1, perPage: Int = 20): Single<List<MyHeaderImage>>
}