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

import com.furianrt.mydiary.data.repository.image.ImageRepository
import javax.inject.Inject

class IsDailyImageEnabledUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    fun invoke(): Boolean = imageRepository.isDailyImageEnabled()
}