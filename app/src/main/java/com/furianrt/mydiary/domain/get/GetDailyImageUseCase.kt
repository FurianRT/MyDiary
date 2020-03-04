/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.model.entity.MyHeaderImage
import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import com.furianrt.mydiary.model.gateway.image.ImageGateway
import io.reactivex.Single
import net.danlew.android.joda.DateUtils
import org.joda.time.DateTime
import javax.inject.Inject

class GetDailyImageUseCase @Inject constructor(
        private val imageGateway: ImageGateway,
        private val deviceGateway: DeviceGateway
) {

    operator fun invoke(): Single<MyHeaderImage> {
        val category = imageGateway.getDailyImageCategory()
        val networkAvailable = deviceGateway.isNetworkAvailable()
        return imageGateway.getHeaderImages()
                .first(emptyList())
                .flatMap { dbImages ->
                    return@flatMap when {
                        dbImages.isNotEmpty() && DateUtils.isToday(DateTime(dbImages.first().addedTime)) ->
                            Single.just(dbImages.first())
                        dbImages.isNotEmpty() && !networkAvailable ->
                            Single.just(dbImages.first())
                        dbImages.isEmpty() && networkAvailable ->
                            imageGateway.loadHeaderImages(category)
                                    .map { it.first() }
                                    .flatMapCompletable { imageGateway.insertHeaderImage(it) }
                                    .andThen(imageGateway.getHeaderImages().firstOrError())
                                    .map { it.first() }
                        dbImages.isNotEmpty() && networkAvailable ->
                            imageGateway.loadHeaderImages(category)
                                    .onErrorReturn { dbImages }
                                    .map { apiImages -> apiImages.find { apiImage -> dbImages.find { it.id == apiImage.id } == null } ?: dbImages.first() }
                                    .flatMapCompletable { imageGateway.insertHeaderImage(it) }
                                    .andThen(imageGateway.getHeaderImages().firstOrError())
                                    .map { it.first() }
                        else ->
                            throw Exception()
                    }
                }
    }
}