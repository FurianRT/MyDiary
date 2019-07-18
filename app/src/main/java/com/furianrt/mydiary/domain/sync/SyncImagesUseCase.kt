package com.furianrt.mydiary.domain.sync

import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.repository.image.ImageRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class SyncImagesUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    class SyncImagesException : Throwable()

    fun invoke(email: String): Completable =
            imageRepository.getAllImages()
                    .first(emptyList())
                    .map { images -> images.filter { !it.isSync(email) } }
                    .map { notSyncImages -> notSyncImages.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { images ->
                        val notSyncFiles = images.filter { !it.isFileSync(email) }
                        notSyncFiles.forEach { it.fileSyncWith.add(email) }
                        Completable.concat(listOf(
                                imageRepository.saveImagesFilesInCloud(notSyncFiles),
                                imageRepository.saveImagesInCloud(images),
                                imageRepository.updateImageSync(images)
                        ))
                    }
                    .andThen(imageRepository.getDeletedImages().first(emptyList()))
                    .flatMapCompletable { imageRepository.deleteImagesFromCloud(it) }
                    .andThen(Single.zip(
                            imageRepository.getAllImagesFromCloud(),
                            imageRepository.getAllImages().firstOrError(),
                            BiFunction<List<MyImage>, List<MyImage>, List<MyImage>> { cloudImages, dbImages ->
                                cloudImages.toMutableList().apply {
                                    dbImages.forEach { image -> removeAll { it.name == image.name } }
                                }
                            }
                    ))
                    .flatMapCompletable { images ->
                        Completable.concat(listOf(
                                imageRepository.loadImageFiles(images),
                                imageRepository.insertImages(images)
                        ))
                    }
                    .onErrorResumeNext { Completable.error(SyncImagesException()) }
}