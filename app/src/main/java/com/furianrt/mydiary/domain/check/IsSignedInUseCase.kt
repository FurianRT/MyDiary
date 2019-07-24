package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import javax.inject.Inject

class IsSignedInUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    fun invoke(): Boolean = profileRepository.isSignedIn()
}