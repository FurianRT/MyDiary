/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain

import com.furianrt.mydiary.model.entity.MyLocation
import com.furianrt.mydiary.model.repository.device.DeviceRepository
import io.reactivex.Maybe
import javax.inject.Inject

class FindLocationUseCase @Inject constructor(
        private val deviceRepository: DeviceRepository
) {

    private var mCallback: DeviceRepository.OnLocationFoundCallback? = null

    fun invoke(): Maybe<MyLocation> = Maybe.create<MyLocation> { emitter ->
        if (deviceRepository.isLocationAvailable()) {
            mCallback = object : DeviceRepository.OnLocationFoundCallback {
                override fun onLocationFound(location: MyLocation) {
                    emitter.onSuccess(location)
                    mCallback?.let { deviceRepository.removeLocationCallback(it) }
                }
            }
            mCallback?.let { deviceRepository.findLocation(it) }
        } else {
            emitter.onComplete()
        }
    }.doOnDispose {
        mCallback?.let { deviceRepository.removeLocationCallback(it) }
    }
}