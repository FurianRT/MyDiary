package com.furianrt.mydiary.domain.auth

import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import io.reactivex.Completable
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    fun invoke(email: String, password: String): Completable =
            profileRepository.signUp(email, password)
}