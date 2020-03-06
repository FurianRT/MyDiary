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

import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import io.reactivex.Single
import javax.inject.Inject

class UriToRealPathUseCase @Inject constructor(
        private val deviceGateway: DeviceGateway
) {

    class UriConvertException : Throwable()

    operator fun invoke(uri: String): Single<String> = Single.create { emitter ->
        val callback = object : DeviceGateway.OnUriConvertCallback {
            override fun onUriRealPathError() {
                throw UriConvertException()
            }

            override fun onUriRealPathReceived(path: String) {
                emitter.onSuccess(path)
            }
        }
        deviceGateway.getRealPathFromUri(uri, callback)
    }
}