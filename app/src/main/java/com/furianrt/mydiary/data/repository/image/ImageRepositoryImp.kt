/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.repository.image

import android.graphics.Bitmap
import com.furianrt.mydiary.data.entity.MyHeaderImage
import com.furianrt.mydiary.data.entity.MyImage
import com.furianrt.mydiary.data.source.api.images.ImageApiService
import com.furianrt.mydiary.data.source.auth.AuthHelper
import com.furianrt.mydiary.data.source.cloud.CloudHelper
import com.furianrt.mydiary.data.source.database.HeaderImageDao
import com.furianrt.mydiary.data.source.database.ImageDao
import com.furianrt.mydiary.data.source.preferences.PreferencesHelper
import com.furianrt.mydiary.data.source.storage.StorageHelper
import com.furianrt.mydiary.utils.MyRxUtils
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.*
import org.joda.time.DateTime
import javax.inject.Inject

class ImageRepositoryImp @Inject constructor(
        private val imageDao: ImageDao,
        private val headerImageDao: HeaderImageDao,
        private val prefs: PreferencesHelper,
        private val storage: StorageHelper,
        private val imageApi: ImageApiService,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : ImageRepository {

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
            Single.fromCallable { storage.copyImageToStorage(image.uri, image.name) }
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
            imageApi.getImages(category, page, perPage)
                    .map { response ->
                        response.images
                                .map { MyHeaderImage(it.id, it.largeImageURL, DateTime.now().millis) }
                                .sortedByDescending { it.addedTime }
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
}