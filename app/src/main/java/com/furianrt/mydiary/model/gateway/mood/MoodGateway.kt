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
import io.reactivex.Flowable

interface MoodGateway {
    fun getMood(moodId: Int): Flowable<MyMood>
    fun getAllMoods(): Flowable<List<MyMood>>
    fun isMoodEnabled(): Boolean
}