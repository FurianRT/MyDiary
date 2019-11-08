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
import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import io.reactivex.Maybe
import javax.inject.Inject

class FindLocationUseCase @Inject constructor(
        private val deviceGateway: DeviceGateway
) {

    private var mCallback: DeviceGateway.OnLocationFoundCallback? = null

    fun invoke(): Maybe<MyLocation> = Maybe.create<MyLocation> { emitter ->
        if (deviceGateway.isLocationAvailable()) {
            mCallback = object : DeviceGateway.OnLocationFoundCallback {
                override fun onLocationFound(location: MyLocation) {
                    emitter.onSuccess(location)
                    mCallback?.let { deviceGateway.removeLocationCallback(it) }
                }
            }
            mCallback?.let { deviceGateway.findLocation(it) }
        } else {
            emitter.onComplete()
        }
    }.doOnDispose {
        mCallback?.let { deviceGateway.removeLocationCallback(it) }
    }
}