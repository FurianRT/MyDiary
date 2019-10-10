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
import com.furianrt.mydiary.data.entity.MyImage
import com.furianrt.mydiary.data.repository.image.ImageRepository
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class SaveImagesUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    fun invoke(noteId: String, imageUrls: List<String>): Completable =
            Flowable.fromIterable(imageUrls)
                    .map { url ->
                        val name = noteId + "_" + generateUniqueId()
                        return@map MyImage(name, url, noteId)
                    }
                    .flatMapSingle { image -> imageRepository.saveImageToStorage(image) }
                    .flatMapSingle { savedImage ->
                        imageRepository.insertImage(savedImage).toSingleDefault(true)
                    }
                    .onErrorReturn { false }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()

    fun invoke(noteId: String, bitmap: Bitmap): Completable {
        val image = MyImage(noteId + "_" + generateUniqueId(), "", noteId)
        return imageRepository.saveBitmapToStorage(bitmap, image)
                .flatMapCompletable { imageRepository.insertImage(it) }
                .onErrorComplete()
    }
}