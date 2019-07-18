package com.furianrt.mydiary.domain.save

import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.repository.image.ImageRepository
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Completable
import io.reactivex.Flowable
import org.joda.time.DateTime
import javax.inject.Inject

class SaveImagesUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    fun invoke(noteId: String, imageUrls: List<String>): Completable =
            Flowable.fromIterable(imageUrls)
                    .map { url ->
                        val name = noteId + "_" + generateUniqueId()
                        return@map MyImage(name, url, noteId, DateTime.now().millis)
                    }
                    .flatMapSingle { image -> imageRepository.saveImageToStorage(image) }
                    .flatMapSingle { savedImage ->
                        imageRepository.insertImage(savedImage).toSingleDefault(true)
                    }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
}