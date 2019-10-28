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
import com.furianrt.mydiary.model.repository.device.DeviceRepository
import com.furianrt.mydiary.model.repository.image.ImageRepository
import com.furianrt.mydiary.domain.UriToRealPathUseCase
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class SaveImagesUseCase @Inject constructor(
        private val imageRepository: ImageRepository,
        private val deviceRepository: DeviceRepository,
        private val uriToRealPath: UriToRealPathUseCase
) {

    fun invoke(noteId: String, imageUris: List<String>): Completable =
            Flowable.fromIterable(imageUris)
                    .flatMapSingle { uriToRealPath.invoke(it).onErrorReturn { "" } }
                    .collectInto(mutableListOf<String>()) { l, i -> l.add(i) }
                    .map { it.filter { uri -> uri.isNotEmpty() } }
                    .flatMapPublisher { Flowable.fromIterable(it) }
                    .map { path ->
                        val name = noteId + "_" + generateUniqueId()
                        return@map MyImage(name, path, noteId)
                    }
                    .flatMapSingle { image -> imageRepository.saveImageToStorage(image) }
                    .flatMapSingle { savedImage ->
                        imageRepository.insertImage(savedImage).toSingleDefault(true)
                    }
                    .onErrorReturn { false }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .flatMapCompletable { Completable.fromAction { deviceRepository.clearUriTempFiles() } }

    fun invoke(noteId: String, bitmap: Bitmap): Completable {
        val image = MyImage(noteId + "_" + generateUniqueId(), "", noteId)
        return imageRepository.saveBitmapToStorage(bitmap, image)
                .flatMapCompletable { imageRepository.insertImage(it) }
                .onErrorComplete()
    }
}