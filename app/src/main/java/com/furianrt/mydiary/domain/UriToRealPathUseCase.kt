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

    private var mCallback: DeviceGateway.OnUriConvertCallback? = null

    fun invoke(uri: String): Single<String> = Single.create<String> {  emitter ->
        mCallback = object : DeviceGateway.OnUriConvertCallback {
            override fun onUriRealPathError() {
                emitter.onError(UriConvertException())
                mCallback?.let { deviceGateway.removeUriConvertCallback(it) }
            }

            override fun onUriRealPathReceived(path: String) {
                emitter.onSuccess(path)
                mCallback?.let { deviceGateway.removeUriConvertCallback(it) }
            }
        }
        mCallback?.let { deviceGateway.getRealPathFromUri(uri, it) }
    }.doOnDispose {
        mCallback?.let { deviceGateway.removeUriConvertCallback(it) }
    }
}