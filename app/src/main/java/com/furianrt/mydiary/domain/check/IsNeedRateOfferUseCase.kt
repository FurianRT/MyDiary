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

import com.furianrt.mydiary.model.gateway.general.GeneralGateway
import com.furianrt.mydiary.di.application.component.AppScope
import javax.inject.Inject

@AppScope
class IsNeedRateOfferUseCase @Inject constructor(
        private val generalGateway: GeneralGateway
) {

    private var mIsAlreadyShown = false

    operator fun invoke(): Boolean {
        val need = generalGateway.isRateOfferEnabled()
                && generalGateway.getNumberOfLaunches() % 5 == 0
                && !mIsAlreadyShown

        mIsAlreadyShown = true

        return need
    }
}