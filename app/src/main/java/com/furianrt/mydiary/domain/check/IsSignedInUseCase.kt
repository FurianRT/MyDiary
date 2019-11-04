/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.model.gateway.profile.ProfileGateway
import javax.inject.Inject

class IsSignedInUseCase @Inject constructor(
        private val profileGateway: ProfileGateway
) {

    fun invoke(): Boolean = profileGateway.isSignedIn()
}