package com.furianrt.mydiary.domain

import com.furianrt.mydiary.data.repository.pin.PinRepository
import javax.inject.Inject

class IsPinEnabledUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(): Boolean = pinRepository.isPinEnabled()
}