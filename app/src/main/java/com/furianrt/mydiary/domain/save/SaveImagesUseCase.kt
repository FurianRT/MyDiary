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
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class SaveImagesUseCase @Inject constructor(
        private val imageGateway: ImageGateway,
        private val deviceGateway: DeviceGateway,
        private val uriToRealPathUseCase: UriToRealPathUseCase
) {

    operator fun invoke(noteId: String, imageUris: List<String>): Completable =
            Flowable.fromIterable(imageUris)
                    .flatMapSingle { uri ->
                        uriToRealPathUseCase(uri)
                                .map { path ->
                                    val name = noteId + "_" + generateUniqueId()
                                    MyImage(name, path, noteId)
                                }
                                .flatMap { image -> imageGateway.saveImageToStorage(image) }
                                .flatMapCompletable { imageGateway.insertImage(it) }
                                .andThen(
                                        Completable.fromAction { deviceGateway.clearUriTempFiles() }
                                                .toSingleDefault(true)
                                )
                                .onErrorReturn { false }
                    }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .flatMapCompletable { Completable.fromAction { deviceGateway.clearUriTempFiles() } }

    operator fun invoke(noteId: String, bitmap: Bitmap): Completable {
        val image = MyImage(noteId + "_" + generateUniqueId(), "", noteId)
        return imageGateway.saveBitmapToStorage(bitmap, image)
                .flatMapCompletable { imageGateway.insertImage(it) }
                .onErrorComplete()
    }
}