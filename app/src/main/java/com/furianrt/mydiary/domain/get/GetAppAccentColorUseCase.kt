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

import com.furianrt.mydiary.model.repository.general.GeneralRepository
import javax.inject.Inject

class GetAppAccentColorUseCase @Inject constructor(
        private val generalRepository: GeneralRepository
) {

    fun invoke(): Int = generalRepository.getAccentColor()
}