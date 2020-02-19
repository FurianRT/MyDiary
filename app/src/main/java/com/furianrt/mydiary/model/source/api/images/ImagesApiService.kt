/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.api.images

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ImagesApiService {

    @GET(".")
    fun getImages(
            @Query("category") category: String,
            @Query("page") page: Int = 1,
            @Query("per_page") perPage: Int = 20
    ): Single<ImagesResponse>
}