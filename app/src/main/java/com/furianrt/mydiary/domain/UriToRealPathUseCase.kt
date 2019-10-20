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

import com.furianrt.mydiary.data.repository.device.DeviceRepository
import io.reactivex.Single
import javax.inject.Inject

class UriToRealPathUseCase @Inject constructor(
        private val deviceRepository: DeviceRepository
) {

    class UriConvertException : Throwable()

    private var mCallback: DeviceRepository.OnUriConvertCallback? = null

    fun invoke(uri: String): Single<String> = Single.create<String> {  emitter ->
        mCallback = object : DeviceRepository.OnUriConvertCallback {
            override fun onUriRealPathError() {
                emitter.onError(UriConvertException())
                mCallback?.let { deviceRepository.removeUriConvertCallback(it) }
            }

            override fun onUriRealPathReceived(path: String) {
                emitter.onSuccess(path)
                mCallback?.let { deviceRepository.removeUriConvertCallback(it) }
            }
        }
        mCallback?.let { deviceRepository.getRealPathFromUri(uri, it) }
    }.doOnDispose {
        mCallback?.let { deviceRepository.removeUriConvertCallback(it) }
    }
}