/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain

import com.furianrt.mydiary.model.gateway.general.GeneralGateway
import javax.inject.Inject

class IncrementLaunchCountUseCase @Inject constructor(
        private val generalGateway: GeneralGateway
) {

    operator fun invoke() {
        generalGateway.setNumberOfLaunches(generalGateway.getNumberOfLaunches() + 1)
    }
}