package com.furianrt.mydiary.usecase.auth

import com.furianrt.mydiary.data.repository.pin.PinRepository
import javax.inject.Inject

class AuthorizeUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(authorize: Boolean) {
        pinRepository.setAuthorized(authorize)
    }
}