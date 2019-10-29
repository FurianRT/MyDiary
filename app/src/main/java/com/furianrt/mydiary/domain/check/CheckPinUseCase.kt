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

import com.furianrt.mydiary.model.repository.pin.PinRepository
import io.reactivex.Single
import javax.inject.Inject

class CheckPinUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(newPin: String): Single<Boolean> =
            pinRepository.getPin().map { oldPin -> newPin == oldPin }
}