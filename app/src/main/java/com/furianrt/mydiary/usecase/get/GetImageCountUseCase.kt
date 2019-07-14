package com.furianrt.mydiary.usecase.get

import com.furianrt.mydiary.data.repository.image.ImageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetImageCountUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    fun invoke(): Flowable<Int> = imageRepository.getImageCount()
}