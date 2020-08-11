/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.sync

import com.furianrt.mydiary.model.entity.MyImage
import com.furianrt.mydiary.model.gateway.image.ImageGateway
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import javax.inject.Inject

class SyncImagesUseCase @Inject constructor(
        private val imageGateway: ImageGateway
) {

    class SyncImagesException : Throwable()

    operator fun invoke(email: String): Completable =
            imageGateway.getAllImages()
                    .firstOrError()
                    .map { images -> images.filter { !it.isSync(email) } }
                    .map { notSyncImages -> notSyncImages.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { images ->
                        val notSyncFiles = images.filter { !it.isFileSync(email) }
                        notSyncFiles.forEach { it.fileSyncWith.add(email) }
                        Completable.concat(listOf(
                                imageGateway.saveImagesFilesInCloud(notSyncFiles),
                                imageGateway.saveImagesInCloud(images),
                                imageGateway.updateImageSync(images)
                        ))
                    }
                    .andThen(imageGateway.getDeletedImages().firstOrError())
                    .flatMapCompletable { imageGateway.deleteImagesFromCloud(it).onErrorComplete() }
                    .andThen(Single.zip(
                            imageGateway.getAllImagesFromCloud(),
                            imageGateway.getAllImages().firstOrError(),
                            BiFunction<List<MyImage>, List<MyImage>, List<MyImage>> { cloudImages, dbImages ->
                                cloudImages.toMutableList().apply {
                                    dbImages.forEach { image -> removeAll { it.name == image.name } }
                                }
                            }
                    ))
                    .flatMapCompletable { images ->
                        val path = imageGateway.getAvailableImageDirectory()
                        val imagesWithPath = images.map { it.apply { this.path = "$path/$name" } }
                        Completable.concat(listOf(
                                imageGateway.loadImageFiles(imagesWithPath),
                                imageGateway.insertImages(imagesWithPath)
                        ))
                    }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncImagesException())
                    }
}