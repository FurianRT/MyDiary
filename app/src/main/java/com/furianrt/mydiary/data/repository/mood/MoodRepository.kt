/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.repository.mood

import com.furianrt.mydiary.data.entity.MyMood
import io.reactivex.Single

interface MoodRepository {
    fun getMood(moodId: Int): Single<MyMood>
    fun getAllMoods(): Single<List<MyMood>>
    fun isMoodEnabled(): Boolean
}