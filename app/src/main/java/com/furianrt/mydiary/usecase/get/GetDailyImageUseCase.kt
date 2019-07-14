package com.furianrt.mydiary.usecase.get

import com.furianrt.mydiary.data.model.MyHeaderImage
import com.furianrt.mydiary.data.repository.image.ImageRepository
import io.reactivex.Single
import net.danlew.android.joda.DateUtils
import org.joda.time.DateTime
import javax.inject.Inject

class GetDailyImageUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    fun invoke(networkAvailable: Boolean): Single<MyHeaderImage> {
        val category = imageRepository.getDailyImageCategory()
        return imageRepository.getHeaderImages()
                .first(emptyList())
                .flatMap { dbImages ->
                    return@flatMap when {
                        dbImages.isNotEmpty() && DateUtils.isToday(DateTime(dbImages.first().addedTime)) ->
                            Single.just(dbImages.first())
                        dbImages.isNotEmpty() && networkAvailable ->
                            Single.just(dbImages.first())
                        dbImages.isEmpty() && networkAvailable ->
                            imageRepository.loadHeaderImages(category)
                                    .map { it.first() }
                                    .flatMapCompletable { imageRepository.insertHeaderImage(it) }
                                    .andThen(imageRepository.getHeaderImages().firstOrError())
                                    .map { it.first() }
                        dbImages.isNotEmpty() && networkAvailable ->
                            imageRepository.loadHeaderImages(category)
                                    .onErrorReturn { dbImages }
                                    .map { list ->
                                        list.find { apiImage ->
                                            dbImages.find { it.id == apiImage.id } == null
                                        } ?: dbImages.first()
                                    }
                                    .flatMapCompletable { imageRepository.insertHeaderImage(it) }
                                    .andThen(imageRepository.getHeaderImages().firstOrError())
                                    .map { it.first() }
                        else ->
                            throw Exception()
                    }
                }
    }

}