/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

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