/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.send

import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import com.furianrt.mydiary.model.gateway.pin.PinGateway
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SendPinResetEmailUseCase @Inject constructor(
        private val pinGateway: PinGateway,
        private val deviceGateway: DeviceGateway
) {

    class NetworkNotAvailableException : Throwable()

    operator fun invoke(): Completable =
            Single.fromCallable { deviceGateway.isNetworkAvailable() }
                    .flatMapCompletable { networkAvailable ->
                        if (networkAvailable) {
                            pinGateway.sendPinResetEmail()
                        } else {
                            throw NetworkNotAvailableException()
                        }
                    }
}