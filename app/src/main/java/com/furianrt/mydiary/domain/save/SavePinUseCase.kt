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

import com.furianrt.mydiary.model.gateway.pin.PinGateway
import io.reactivex.Completable
import javax.inject.Inject

class SavePinUseCase @Inject constructor(
        private val pinGateway: PinGateway
) {

    fun invoke(pin: String, email: String): Completable {
        pinGateway.setBackupEmail(email)
        return pinGateway.setPin(pin)
    }
}