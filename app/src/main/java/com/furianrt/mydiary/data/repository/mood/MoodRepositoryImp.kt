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

import com.furianrt.mydiary.data.database.MoodDao
import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class MoodRepositoryImp @Inject constructor(
        private val moodDao: MoodDao,
        private val prefs: PreferencesHelper,
        private val rxScheduler: Scheduler
) : MoodRepository {

    override fun getMood(moodId: Int): Single<MyMood> =
            moodDao.getMood(moodId)
                    .subscribeOn(rxScheduler)

    override fun getAllMoods(): Single<List<MyMood>> =
            moodDao.getAllMoods()
                    .subscribeOn(rxScheduler)

    override fun isMoodEnabled(): Boolean = prefs.isMoodEnabled()
}