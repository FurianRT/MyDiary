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

import com.furianrt.mydiary.data.repository.profile.ProfileRepository
import io.reactivex.Observable
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(
        private val profileRepository: ProfileRepository
) {

    companion object {
        const val STATE_SIGN_OUT = 0
        const val STATE_SIGN_IN = 1
    }

    fun invoke(): Observable<Int> = profileRepository.observeAuthState()
            .map { state ->
                when (state) {
                    ProfileRepository.SIGN_STATE_SIGN_OUT -> STATE_SIGN_OUT
                    ProfileRepository.SIGN_STATE_SIGN_IN -> STATE_SIGN_IN
                    else -> throw IllegalStateException()
                }
            }
}