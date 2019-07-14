package com.furianrt.mydiary.usecase.check

import com.furianrt.mydiary.data.repository.pin.PinRepository
import javax.inject.Inject

class IsFingerprintAvailableUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(): Boolean = pinRepository.isFingerprintEnabled()
}