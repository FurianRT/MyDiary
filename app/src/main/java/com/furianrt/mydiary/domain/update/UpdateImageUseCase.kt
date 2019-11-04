/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.update

import com.furianrt.mydiary.model.entity.MyImage
import com.furianrt.mydiary.model.gateway.image.ImageGateway
import io.reactivex.Completable
import javax.inject.Inject

class UpdateImageUseCase @Inject constructor(
        private val imageGateway: ImageGateway
) {

    fun invoke(image: MyImage): Completable = imageGateway.updateImage(image)

    fun invoke(images: List<MyImage>): Completable = imageGateway.updateImage(images)
}