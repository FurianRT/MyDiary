/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.entity.MyProfile
import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    fun invoke(): Observable<MyProfile> = profileRepository.getDbProfile()
}