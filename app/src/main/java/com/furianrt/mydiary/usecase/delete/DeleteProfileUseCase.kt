package com.furianrt.mydiary.usecase.delete

import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteProfileUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    fun invoke(): Completable = profileRepository.clearDbProfile()
}