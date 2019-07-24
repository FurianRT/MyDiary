package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.data.repository.image.ImageRepository
import javax.inject.Inject

class IsDailyImageEnabledUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    fun invoke(): Boolean = imageRepository.isDailyImageEnabled()
}