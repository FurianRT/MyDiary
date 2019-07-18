package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.model.MyProfile
import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    fun invoke(): Observable<MyProfile> = profileRepository.getDbProfile()
}