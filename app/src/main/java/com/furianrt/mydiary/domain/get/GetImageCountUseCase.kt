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

import com.furianrt.mydiary.model.gateway.image.ImageGateway
import io.reactivex.Flowable
import javax.inject.Inject

class GetImageCountUseCase @Inject constructor(
        private val imageGateway: ImageGateway
) {

    fun invoke(): Flowable<Int> = imageGateway.getImageCount()
}