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

import com.furianrt.mydiary.model.gateway.profile.ProfileGateway
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
        private val profileGateway: ProfileGateway
) {

    operator fun invoke(email: String, password: String): Completable =
            profileGateway.signUp(email, password)
}