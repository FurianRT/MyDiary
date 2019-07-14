package com.furianrt.mydiary.usecase.check

import com.furianrt.mydiary.data.repository.pin.PinRepository
import io.reactivex.Single
import javax.inject.Inject

class CheckPinUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(newPin: String): Single<Boolean> =
            pinRepository.getPin().map { oldPin -> newPin == oldPin }
}