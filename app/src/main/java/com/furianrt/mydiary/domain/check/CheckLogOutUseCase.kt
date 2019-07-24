package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import io.reactivex.Completable
import javax.inject.Inject

class CheckLogOutUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    fun invoke(): Completable =
            profileRepository.getDbProfileCount()
                    .flatMapCompletable { count ->
                        if (profileRepository.isSignedIn() && count == 0) {
                            profileRepository.signOut()
                        } else if (count > 1) {
                            profileRepository.signOut().andThen(profileRepository.clearDbProfile())
                        } else {
                            Completable.complete()
                        }
                    }
}