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

import com.furianrt.mydiary.data.entity.MyMood
import com.furianrt.mydiary.data.repository.mood.MoodRepository
import io.reactivex.Single
import javax.inject.Inject

class GetMoodsUseCase @Inject constructor(
        private val moodRepository: MoodRepository
) {

    fun invoke(): Single<List<MyMood>> = moodRepository.getAllMoods()

    fun invoke(moodId: Int): Single<MyMood> = moodRepository.getMood(moodId)
}