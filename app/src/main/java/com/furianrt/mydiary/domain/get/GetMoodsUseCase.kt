package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.data.repository.mood.MoodRepository
import io.reactivex.Single
import javax.inject.Inject

class GetMoodsUseCase @Inject constructor(
        private val moodRepository: MoodRepository
) {

    fun invoke(): Single<List<MyMood>> = moodRepository.getAllMoods()

    fun invoke(moodId: Int): Single<MyMood> = moodRepository.getMood(moodId)
}