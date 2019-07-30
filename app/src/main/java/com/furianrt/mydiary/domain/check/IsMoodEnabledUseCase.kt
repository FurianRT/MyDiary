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

import com.furianrt.mydiary.data.repository.mood.MoodRepository
import javax.inject.Inject

class IsMoodEnabledUseCase @Inject constructor(
        private val moodRepository: MoodRepository
) {

    fun invoke(): Boolean = moodRepository.isMoodEnabled()
}