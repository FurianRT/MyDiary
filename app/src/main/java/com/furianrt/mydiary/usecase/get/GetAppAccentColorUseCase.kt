package com.furianrt.mydiary.usecase.get

import com.furianrt.mydiary.data.repository.general.GeneralRepository
import javax.inject.Inject

class GetAppAccentColorUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    fun invoke(): Int = generalRepository.getAccentColor()
}