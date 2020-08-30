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
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class GetMoodsUseCase @Inject constructor(
        private val moodGateway: MoodGateway
) {

    operator fun invoke(): Flowable<List<MyMood>> = moodGateway.getAllMoods()

    operator fun invoke(moodId: Int): Flowable<Optional<MyMood>> = moodGateway.getMood(moodId)
}