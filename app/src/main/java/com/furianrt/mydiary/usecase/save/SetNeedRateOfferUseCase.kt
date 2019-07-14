package com.furianrt.mydiary.usecase.save

import com.furianrt.mydiary.data.repository.general.GeneralRepository
import javax.inject.Inject

class SetNeedRateOfferUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    fun invoke(need: Boolean) {
        generalRepository.enableRateOffer(need)
    }
}