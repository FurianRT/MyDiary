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

import com.furianrt.mydiary.data.repository.pin.PinRepository
import javax.inject.Inject

class IsPinEnabledUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(): Boolean = pinRepository.isPinEnabled()
}