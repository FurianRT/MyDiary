package com.furianrt.mydiary.domain.auth

import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import io.reactivex.Completable
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    fun invoke(): Completable = profileRepository.signOut()
}