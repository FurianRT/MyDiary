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

import com.furianrt.mydiary.model.entity.MyMood
import com.furianrt.mydiary.model.gateway.mood.MoodGateway
import io.reactivex.Flowable
import javax.inject.Inject

class GetMoodsUseCase @Inject constructor(
        private val moodGateway: MoodGateway
) {

    fun invoke(): Flowable<List<MyMood>> = moodGateway.getAllMoods()

    fun invoke(moodId: Int): Flowable<MyMood> = moodGateway.getMood(moodId)
}