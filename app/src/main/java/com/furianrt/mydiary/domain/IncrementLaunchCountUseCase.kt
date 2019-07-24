package com.furianrt.mydiary.domain

import com.furianrt.mydiary.data.repository.general.GeneralRepository
import javax.inject.Inject

class IncrementLaunchCountUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    fun invoke() {
        generalRepository.setNumberOfLaunches(generalRepository.getNumberOfLaunches() + 1)
    }
}