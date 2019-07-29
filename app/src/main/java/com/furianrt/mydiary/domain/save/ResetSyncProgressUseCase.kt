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

import com.furianrt.mydiary.data.repository.general.GeneralRepository
import org.joda.time.DateTime
import javax.inject.Inject

class ResetSyncProgressUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    companion object {
        private const val SYNC_PROGRESS_RESET_TIME = 1000 * 60
    }

    fun invoke() {
        val currentTime = DateTime.now().millis
        val launchTimeDiff = currentTime - generalRepository.getLastAppLaunchTime()
        if (launchTimeDiff >= SYNC_PROGRESS_RESET_TIME) {
            generalRepository.setLastSyncMessage(null)
        }
        generalRepository.setLastAppLaunchTime(currentTime)
    }
}