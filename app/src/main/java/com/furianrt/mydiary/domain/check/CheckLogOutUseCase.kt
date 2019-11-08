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
import io.reactivex.Completable
import javax.inject.Inject

class CheckLogOutUseCase @Inject constructor(
        private val profileGateway: ProfileGateway
) {

    fun invoke(): Completable =
            profileGateway.getDbProfileCount()
                    .flatMapCompletable { count ->
                        if (profileGateway.isSignedIn() && count == 0) {
                            profileGateway.signOut()
                        } else if (count > 1) {
                            profileGateway.signOut().andThen(profileGateway.clearDbProfile())
                        } else {
                            Completable.complete()
                        }
                    }
}