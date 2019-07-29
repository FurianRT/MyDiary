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

import com.furianrt.mydiary.data.repository.device.DeviceRepository
import com.furianrt.mydiary.data.repository.pin.PinRepository
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class SendPinResetEmailUseCase @Inject constructor(
        private val pinRepository: PinRepository,
        private val deviceRepository: DeviceRepository
) {

    class NetworkNotAvailableException : Throwable()

    fun invoke(): Completable =
            Single.fromCallable { deviceRepository.isNetworkAvailable() }
                    .flatMapCompletable { networkAvailable ->
                        if (networkAvailable) {
                            pinRepository.sendPinResetEmail()
                        } else {
                            throw NetworkNotAvailableException()
                        }
                    }
}