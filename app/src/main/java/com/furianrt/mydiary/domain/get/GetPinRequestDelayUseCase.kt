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

import com.furianrt.mydiary.model.repository.pin.PinRepository
import javax.inject.Inject

class GetPinRequestDelayUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(): Long = pinRepository.getPinRequestDelay()
}