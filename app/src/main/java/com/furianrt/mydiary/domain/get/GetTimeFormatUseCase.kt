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

import com.furianrt.mydiary.model.gateway.general.GeneralGateway
import javax.inject.Inject

class GetTimeFormatUseCase @Inject constructor(
        private val generalGateway: GeneralGateway
) {

    companion object {
        const val TIME_FORMAT_12 = 0
        const val TIME_FORMAT_24 = 1
    }

    operator fun invoke(): Int = if (generalGateway.is24TimeFormat()) {
        TIME_FORMAT_24
    } else {
        TIME_FORMAT_12
    }
}