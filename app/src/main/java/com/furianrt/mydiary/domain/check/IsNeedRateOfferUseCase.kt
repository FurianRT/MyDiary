package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.data.repository.general.GeneralRepository
import javax.inject.Inject

class IsNeedRateOfferUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    fun invoke(): Boolean =
            generalRepository.isRateOfferEnabled() && generalRepository.getNumberOfLaunches() % 5 == 0
}