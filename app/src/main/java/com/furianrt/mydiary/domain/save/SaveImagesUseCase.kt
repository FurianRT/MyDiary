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

import android.graphics.Bitmap
import com.furianrt.mydiary.model.entity.MyImage
import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import com.furianrt.mydiary.model.gateway.image.ImageGateway
import com.furianrt.mydiary.domain.UriToRealPathUseCase
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SaveImagesUseCase @Inject constructor(
        private val imageGateway: ImageGateway,
        private val deviceGateway: DeviceGateway,
        private val uriToRealPathUseCase: UriToRealPathUseCase
) {

    operator fun invoke(noteId: String, imageUris: List<String>): Completable =
            Completable.concat(imageUris.map { saveImage(noteId, it) })

    private fun saveImage(noteId: String, imageUri: String): Completable =
            uriToRealPathUseCase(imageUri)
                    .map { MyImage(noteId + "_" + generateUniqueId(), it, noteId) }
                    .flatMap { imageGateway.saveImageToStorage(it) }
                    .flatMapCompletable { imageGateway.insertImage(it) }
                    .andThen(Completable.fromAction { deviceGateway.clearUriTempFiles() })

    operator fun invoke(noteId: String, bitmap: Bitmap): Completable =
            imageGateway.saveBitmapToStorage(bitmap, MyImage(noteId + "_" + generateUniqueId(), "", noteId))
                    .flatMapCompletable { imageGateway.insertImage(it) }
                    .onErrorComplete()

    operator fun invoke(noteId: String, path: String): Completable =
            imageGateway.saveImageToStorage(MyImage(noteId + "_" + generateUniqueId(), path, noteId))
                    .flatMapCompletable { imageGateway.insertImage(it) }
}