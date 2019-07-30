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

import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.repository.device.DeviceRepository
import io.reactivex.Maybe
import javax.inject.Inject

class FindLocationUseCase @Inject constructor(
        private val deviceRepository: DeviceRepository
) {

    private lateinit var mListener: DeviceRepository.OnLocationFoundListener

    fun invoke(): Maybe<MyLocation> = Maybe.create<MyLocation> { emitter ->
        if (deviceRepository.isLocationAvailable()) {
            mListener = object : DeviceRepository.OnLocationFoundListener {
                override fun onLocationFound(location: MyLocation) {
                    emitter.onSuccess(location)
                }
            }
            deviceRepository.findLocation(mListener)
        } else {
            emitter.onComplete()
        }
    }.doOnDispose {
        if (::mListener.isInitialized) {
            deviceRepository.removeLocationListener(mListener)
        }
    }
}