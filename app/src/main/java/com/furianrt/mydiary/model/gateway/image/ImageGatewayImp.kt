/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.image

import android.graphics.Bitmap
import com.furianrt.mydiary.model.entity.MyHeaderImage
import com.furianrt.mydiary.model.entity.MyImage
import com.furianrt.mydiary.model.source.api.images.ImagesApiService
import com.furianrt.mydiary.model.source.auth.AuthSource
import com.furianrt.mydiary.model.source.cloud.CloudSource
import com.furianrt.mydiary.model.source.database.dao.HeaderImageDao
import com.furianrt.mydiary.model.source.database.dao.ImageDao
import com.furianrt.mydiary.model.source.preferences.PreferencesSource
import com.furianrt.mydiary.model.source.storage.StorageSource
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.*
import javax.inject.Inject

class ImageGatewayImp @Inject constructor(
        private val imageDao: ImageDao,
        private val headerImageDao: HeaderImageDao,
        private val prefs: PreferencesSource,
        private val storage: StorageSource,
        private val imagesApi: ImagesApiService,
        private val cloud: CloudSource,
        private val auth: AuthSource,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : ImageGateway {

    override fun insertImage(image: MyImage): Completable =
            imageDao.insert(image)
                    .subscribeOn(scheduler.io())

    override fun insertImages(images: List<MyImage>): Completable =
            imageDao.insert(images)
                    .subscribeOn(scheduler.io())

    override fun insertHeaderImage(headerImage: MyHeaderImage): Completable =
            headerImageDao.insert(headerImage)
                    .subscribeOn(scheduler.io())

    override fun updateImage(image: MyImage): Completable =
            imageDao.update(image.apply { syncWith.clear() })
                    .subscribeOn(scheduler.io())

    override fun updateImage(images: List<MyImage>): Completable =
            imageDao.update(images.map { it.apply { syncWith.clear() } })
                    .subscribeOn(scheduler.io())

    override fun updateImageSync(images: List<MyImage>): Completable =
            imageDao.update(images)
                    .subscribeOn(scheduler.io())

    override fun deleteImage(imageName: String): Completable =
            imageDao.delete(imageName)
                    .andThen(Completable.fromCallable { storage.deleteFile(imageName) })
                    .subscribeOn(scheduler.io())

    override fun deleteImage(imageNames: List<String>): Completable =
            imageDao.delete(imageNames)
                    .andThen(Observable.fromIterable(imageNames))
                    .flatMapSingle { Single.fromCallable { storage.deleteFile(it) } }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
                    .subscribeOn(scheduler.io())

    override fun deleteImageFromStorage(fileName: String): Single<Boolean> =
            Single.fromCallable { storage.deleteFile(fileName) }
                    .subscribeOn(scheduler.io())

    override fun cleanupImages(): Completable =
            imageDao.cleanup()
                    .subscribeOn(scheduler.io())

    override fun getAllImages(): Flowable<List<MyImage>> =
            imageDao.getAllImages()
                    .subscribeOn(scheduler.io())

    override fun getDeletedImages(): Flowable<List<MyImage>> =
            imageDao.getDeletedImages()
                    .subscribeOn(scheduler.io())

    override fun saveImageToStorage(image: MyImage): Single<MyImage> =
            Single.fromCallable { storage.copyImageToStorage(image.path, image.name) }
                    .map { file -> MyImage(file.name, file.toURI().toString(), image.noteId, image.addedTime) }
                    .subscribeOn(scheduler.io())

    override fun saveBitmapToStorage(bitmap: Bitmap, image: MyImage): Single<MyImage> =
            Single.fromCallable { storage.copyBitmapToStorage(bitmap, image.name) }
                    .map { file -> MyImage(file.name, file.toURI().toString(), image.noteId, image.addedTime) }
                    .subscribeOn(scheduler.io())

    override fun getImagesForNote(noteId: String): Flowable<List<MyImage>> =
            imageDao.getImagesForNote(noteId)
                    .subscribeOn(scheduler.io())

    override fun getImageCount(): Flowable<Int> =
            imageDao.getCount()
                    .subscribeOn(scheduler.io())

    override fun getHeaderImages(): Flowable<List<MyHeaderImage>> =
            headerImageDao.getHeaderImages()
                    .map { it.sortedByDescending { image -> image.addedTime } }
                    .subscribeOn(scheduler.io())

    override fun loadHeaderImages(category: String, page: Int, perPage: Int): Single<List<MyHeaderImage>> =
            imagesApi.getImages(category, page, perPage)
                    .map { response ->
                        response.images
                                ?.map { MyHeaderImage(it.id!!, it.largeImageURL!!, System.currentTimeMillis()) }
                                ?.sortedByDescending { it.addedTime }
                                ?: emptyList()
                    }
                    .subscribeOn(scheduler.io())

    override fun saveImagesInCloud(images: List<MyImage>): Completable =
            cloud.saveImages(images, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun saveImagesFilesInCloud(images: List<MyImage>): Completable =
            cloud.saveImagesFiles(images, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun deleteImagesFromCloud(images: List<MyImage>): Completable =
            cloud.deleteImages(images, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getAllImagesFromCloud(): Single<List<MyImage>> =
            cloud.getAllImages(auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun loadImageFiles(images: List<MyImage>): Completable =
            cloud.loadImageFiles(images, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun isDailyImageEnabled(): Boolean = prefs.isDailyImageEnabled()

    override fun getDailyImageCategory(): String = prefs.getDailyImageCategory()

    override fun getAvailableImageDirectory(): String = storage.getAvailablePictureDirectory()

    override fun isPanoramaEnabled(): Boolean = prefs.isPanoramaEnabled()
}