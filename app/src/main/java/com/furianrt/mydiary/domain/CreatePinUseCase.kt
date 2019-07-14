package com.furianrt.mydiary.domain

import com.furianrt.mydiary.data.repository.pin.PinRepository
import io.reactivex.Completable
import javax.inject.Inject

class CreatePinUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(pin: String, email: String): Completable {
        pinRepository.setBackupEmail(email)
        return pinRepository.setPin(pin)
    }
}