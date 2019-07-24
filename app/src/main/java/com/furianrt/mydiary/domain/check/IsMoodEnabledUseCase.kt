package com.furianrt.mydiary.domain.check

import com.furianrt.mydiary.data.repository.mood.MoodRepository
import javax.inject.Inject

class IsMoodEnabledUseCase @Inject constructor(
        private val moodRepository: MoodRepository
) {

    fun invoke(): Boolean = moodRepository.isMoodEnabled()
}