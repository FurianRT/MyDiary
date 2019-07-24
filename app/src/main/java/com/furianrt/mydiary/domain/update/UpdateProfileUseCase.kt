package com.furianrt.mydiary.domain.update

import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    fun invoke(syncTime: Long): Completable =
            profileRepository.getDbProfile()
                    .firstOrError()
                    .flatMapCompletable { profileRepository.updateProfile(it.apply { lastSyncTime = syncTime }) }
}