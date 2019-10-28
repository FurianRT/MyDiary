/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.model.repository.general.GeneralRepository
import com.furianrt.mydiary.di.application.component.AppScope
import javax.inject.Inject

@AppScope
class IsNeedRateOfferUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    private var mIsAlreadyShown = false

    fun invoke(): Boolean {
        val need = generalRepository.isRateOfferEnabled()
                && generalRepository.getNumberOfLaunches() % 5 == 0
                && !mIsAlreadyShown

        mIsAlreadyShown = true

        return need
    }
}