package com.furianrt.mydiary.domain.delete

import com.furianrt.mydiary.data.repository.image.ImageRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteImagesUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    fun invoke(imageNames: List<String>): Completable =
            imageRepository.deleteImage(imageNames)
}