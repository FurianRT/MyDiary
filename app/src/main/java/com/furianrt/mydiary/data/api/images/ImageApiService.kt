package com.furianrt.mydiary.data.api.images

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApiService {

    @GET(".")
    fun getImage(@Query("page") page: Int = 1,
                    @Query("per_page") perPage: Int = 20): Single<ImageResponse>
}