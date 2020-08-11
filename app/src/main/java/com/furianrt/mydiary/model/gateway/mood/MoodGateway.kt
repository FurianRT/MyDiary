/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.mood

import com.furianrt.mydiary.model.entity.MyMood
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Flowable

interface MoodGateway {
    fun getMood(moodId: Int): Flowable<Optional<MyMood>>
    fun getAllMoods(): Flowable<List<MyMood>>
    fun isMoodEnabled(): Boolean
}