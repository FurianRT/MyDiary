/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.save

import com.furianrt.mydiary.model.gateway.general.GeneralGateway
import javax.inject.Inject

class ResetSyncProgressUseCase @Inject constructor(
        private val generalGateway: GeneralGateway
) {

    companion object {
        private const val SYNC_PROGRESS_RESET_TIME = 1000 * 60
    }

    operator fun invoke() {
        val currentTime = System.currentTimeMillis()
        val launchTimeDiff = currentTime - generalGateway.getLastAppLaunchTime()
        if (launchTimeDiff >= SYNC_PROGRESS_RESET_TIME) {
            generalGateway.setLastSyncMessage(null)
        }
        generalGateway.setLastAppLaunchTime(currentTime)
    }
}