package com.furianrt.mydiary.domain.update

import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.repository.image.ImageRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateImageUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    fun invoke(image: MyImage): Completable = imageRepository.updateImage(image)

    fun invoke(images: List<MyImage>): Completable = imageRepository.updateImage(images)
}