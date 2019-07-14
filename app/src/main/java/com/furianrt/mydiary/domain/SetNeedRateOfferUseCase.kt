package com.furianrt.mydiary.domain

import com.furianrt.mydiary.data.repository.general.GeneralRepository
import javax.inject.Inject

class SetNeedRateOfferUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    fun invoke(need: Boolean) {
        generalRepository.setNeedRateOffer(need)
    }
}