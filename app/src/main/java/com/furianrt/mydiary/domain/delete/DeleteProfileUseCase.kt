/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.delete

import com.furianrt.mydiary.model.gateway.profile.ProfileGateway
import io.reactivex.Completable
import javax.inject.Inject

class DeleteProfileUseCase @Inject constructor(
        private val profileGateway: ProfileGateway
) {

    fun invoke(): Completable = profileGateway.clearDbProfile()
}