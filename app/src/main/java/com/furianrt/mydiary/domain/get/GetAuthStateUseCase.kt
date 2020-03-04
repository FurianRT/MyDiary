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

import com.furianrt.mydiary.model.gateway.profile.ProfileGateway
import io.reactivex.Observable
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(
        private val profileGateway: ProfileGateway
) {

    companion object {
        const val STATE_SIGN_OUT = 0
        const val STATE_SIGN_IN = 1
    }

    operator fun invoke(): Observable<Int> = profileGateway.observeAuthState()
            .map { state ->
                when (state) {
                    ProfileGateway.SIGN_STATE_SIGN_OUT -> STATE_SIGN_OUT
                    ProfileGateway.SIGN_STATE_SIGN_IN -> STATE_SIGN_IN
                    else -> throw IllegalStateException()
                }
            }
}