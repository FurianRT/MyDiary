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

import com.furianrt.mydiary.data.repository.pin.PinRepository
import javax.inject.Inject

class AuthorizeUseCase @Inject constructor(
        private val pinRepository: PinRepository
) {

    fun invoke(authorize: Boolean) {
        pinRepository.setAuthorized(authorize)
    }
}