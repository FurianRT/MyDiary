package com.furianrt.mydiary.usecase.get

import com.furianrt.mydiary.data.repository.pin.PinRepository
import javax.inject.Inject

class GetPinRequestDelayUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(): Long = pinRepository.getPinRequestDelay()
}