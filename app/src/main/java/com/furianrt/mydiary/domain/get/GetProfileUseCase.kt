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

import com.furianrt.mydiary.model.entity.MyProfile
import com.furianrt.mydiary.model.gateway.profile.ProfileGateway
import com.google.common.base.Optional
import io.reactivex.Flowable
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
        private val profileGateway: ProfileGateway
) {

    operator fun invoke(): Flowable<Optional<MyProfile>> = profileGateway.getDbProfile()
}