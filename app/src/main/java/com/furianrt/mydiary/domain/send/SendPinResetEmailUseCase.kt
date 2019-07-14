package com.furianrt.mydiary.domain.send

import com.furianrt.mydiary.data.repository.pin.PinRepository
import io.reactivex.Completable
import javax.inject.Inject

class SendPinResetEmailUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(): Completable = pinRepository.sendPinResetEmail()
}