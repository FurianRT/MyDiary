package com.furianrt.mydiary.domain.sync

import com.furianrt.mydiary.data.model.SyncProgressMessage
import com.furianrt.mydiary.data.repository.general.GeneralRepository
import javax.inject.Inject

class SetLastSyncMessageUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    fun invoke(progressMessage: SyncProgressMessage) {
        generalRepository.setLastSyncMessage(progressMessage)
    }
}