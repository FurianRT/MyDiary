package com.furianrt.mydiary.data.repository.image

import com.furianrt.mydiary.data.api.images.ImageApiService
import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.NoteDatabase
import com.furianrt.mydiary.data.model.MyHeaderImage
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.storage.StorageHelper
import io.reactivex.*
import org.joda.time.DateTime
import javax.inject.Inject

class ImageRepositoryImp @Inject constructor(
        private val database: NoteDatabase,
        private val prefs: PreferencesHelper,
        private val storage: StorageHelper,
        private val imageApi: ImageApiService,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : ImageRepository {

    override fun insertImage(image: MyImage): Completable =
            database.imageDao().insert(image)
                    .subscribeOn(rxScheduler)

    override fun insertImages(images: List<MyImage>): Completable =
            database.imageDao().insert(images)
                    .subscribeOn(rxScheduler)

    override fun insertHeaderImage(headerImage: MyHeaderImage): Completable =
            database.headerImageDao().insert(headerImage)
                    .subscribeOn(rxScheduler)

    override fun updateImage(image: MyImage): Completable =
            database.imageDao().update(image.apply { syncWith.clear() })
                    .subscribeOn(rxScheduler)

    override fun updateImage(images: List<MyImage>): Completable =
            database.imageDao().update(images.map { it.apply { syncWith.clear() } })
                    .subscribeOn(rxScheduler)

    override fun updateImageSync(images: List<MyImage>): Completable =
            database.imageDao().update(images)
                    .subscribeOn(rxScheduler)

    override fun deleteImage(imageName: String): Completable =
            database.imageDao().delete(imageName)
                    .andThen(Completable.fromCallable { storage.deleteFile(imageName) })
                    .subscribeOn(rxScheduler)

    override fun deleteImage(imageNames: List<String>): Completable =
            database.imageDao().delete(imageNames)
                    .andThen(Observable.fromIterable(imageNames))
                    .flatMapSingle { Single.fromCallable { storage.deleteFile(it) } }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
                    .subscribeOn(rxScheduler)

    override fun deleteImageFromStorage(fileName: String): Single<Boolean> =
            Single.fromCallable { storage.deleteFile(fileName) }
                    .subscribeOn(rxScheduler)

    override fun cleanupImages(): Completable =
            database.imageDao().cleanup()
                    .subscribeOn(rxScheduler)

    override fun getAllImages(): Flowable<List<MyImage>> =
            database.imageDao()
                    .getAllImages()
                    .subscribeOn(rxScheduler)

    override fun getDeletedImages(): Flowable<List<MyImage>> =
            database.imageDao()
                    .getDeletedImages()
                    .subscribeOn(rxScheduler)

    override fun saveImageToStorage(image: MyImage): Single<MyImage> =
            Single.fromCallable { storage.copyImageToStorage(image.uri, image.name) }
                    .map { file -> MyImage(file.name, file.toURI().toString(), image.noteId, image.addedTime) }
                    .subscribeOn(rxScheduler)

    override fun getImagesForNote(noteId: String): Flowable<List<MyImage>> =
            database.imageDao().getImagesForNote(noteId)
                    .subscribeOn(rxScheduler)

    override fun getImageCount(): Flowable<Int> =
            database.imageDao().getCount()
                    .subscribeOn(rxScheduler)

    override fun getHeaderImages(): Flowable<List<MyHeaderImage>> =
            database.headerImageDao().getHeaderImages()
                    .map { it.sortedByDescending { image -> image.addedTime } }
                    .subscribeOn(rxScheduler)

    override fun loadHeaderImages(category: String, page: Int, perPage: Int): Single<List<MyHeaderImage>> =
            imageApi.getImages(category, page, perPage)
                    .map { response ->
                        response.images
                                .map { MyHeaderImage(it.id, it.largeImageURL, DateTime.now().millis) }
                                .sortedByDescending { it.addedTime }
                    }
                    .subscribeOn(rxScheduler)

    override fun saveImagesInCloud(images: List<MyImage>): Completable =
            cloud.saveImages(images, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveImagesFilesInCloud(images: List<MyImage>): Completable =
            cloud.saveImagesFiles(images, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteImagesFromCloud(images: List<MyImage>): Completable =
            cloud.deleteImages(images, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllImagesFromCloud(): Single<List<MyImage>> =
            cloud.getAllImages(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun loadImageFiles(images: List<MyImage>): Completable =
            cloud.loadImageFiles(images, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun isDailyImageEnabled(): Boolean = prefs.isDailyImageEnabled()

    override fun getDailyImageCategory(): String = prefs.getDailyImageCategory()
}