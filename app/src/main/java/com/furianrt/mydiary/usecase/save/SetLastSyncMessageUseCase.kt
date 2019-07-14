package com.furianrt.mydiary.usecase.save

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