/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.save

import com.furianrt.mydiary.data.repository.pin.PinRepository
import io.reactivex.Completable
import javax.inject.Inject

class SavePinUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(pin: String, email: String): Completable {
        pinRepository.setBackupEmail(email)
        return pinRepository.setPin(pin)
    }
}