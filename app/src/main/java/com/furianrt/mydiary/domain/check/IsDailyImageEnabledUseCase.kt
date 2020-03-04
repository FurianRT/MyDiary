/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.model.gateway.image.ImageGateway
import javax.inject.Inject

class IsDailyImageEnabledUseCase @Inject constructor(
        private val imageGateway: ImageGateway
) {

    operator fun invoke(): Boolean = imageGateway.isDailyImageEnabled()
}