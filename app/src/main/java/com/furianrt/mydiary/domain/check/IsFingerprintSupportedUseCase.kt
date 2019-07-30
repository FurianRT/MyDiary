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

import com.furianrt.mydiary.data.repository.device.DeviceRepository
import javax.inject.Inject

class IsFingerprintSupportedUseCase @Inject constructor(
        private val deviceRepository: DeviceRepository
) {

    fun invoke(): Boolean =
            deviceRepository.isFingerprintHardwareSupported() && deviceRepository.isFingerprintEnabled()
}