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
import io.reactivex.Observable
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
        private val profileGateway: ProfileGateway
) {

    fun invoke(): Observable<MyProfile> = profileGateway.getDbProfile()
}