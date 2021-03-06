/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.update

import com.furianrt.mydiary.domain.get.GetProfileUseCase
import com.furianrt.mydiary.model.gateway.profile.ProfileGateway
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
        private val profileGateway: ProfileGateway,
        private val getProfileUseCase: GetProfileUseCase
) {

    operator fun invoke(syncTime: Long): Completable =
            getProfileUseCase()
                    .firstOrError()
                    .flatMapCompletable { result ->
                        if (result.isPresent) {
                            profileGateway.updateProfile(result.get().apply { lastSyncTime = syncTime })
                        } else {
                            Completable.complete()
                        }
                    }
}