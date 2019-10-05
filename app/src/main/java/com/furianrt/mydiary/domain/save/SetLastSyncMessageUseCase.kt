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

import com.furianrt.mydiary.data.entity.SyncProgressMessage
import com.furianrt.mydiary.data.repository.general.GeneralRepository
import javax.inject.Inject

class SetLastSyncMessageUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    fun invoke(progressMessage: SyncProgressMessage) {
        generalRepository.setLastSyncMessage(progressMessage)
    }
}