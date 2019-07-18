package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.model.SyncProgressMessage
import com.furianrt.mydiary.data.repository.general.GeneralRepository
import javax.inject.Inject

class GetLastSyncMessageUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    fun invoke(): SyncProgressMessage? = generalRepository.getLastSyncMessage()
}