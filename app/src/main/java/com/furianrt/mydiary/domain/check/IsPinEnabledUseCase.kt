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

import com.furianrt.mydiary.model.gateway.pin.PinGateway
import javax.inject.Inject

class IsPinEnabledUseCase @Inject constructor(
        private val pinGateway: PinGateway
) {

    operator fun invoke(): Boolean = pinGateway.isPinEnabled()
}