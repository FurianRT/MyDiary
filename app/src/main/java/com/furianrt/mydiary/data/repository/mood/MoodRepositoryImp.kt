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
import com.furianrt.mydiary.data.source.database.MoodDao
import com.furianrt.mydiary.data.source.preferences.PreferencesHelper
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.Flowable
import javax.inject.Inject

class MoodRepositoryImp @Inject constructor(
        private val moodDao: MoodDao,
        private val prefs: PreferencesHelper,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : MoodRepository {

    override fun getMood(moodId: Int): Flowable<MyMood> =
            moodDao.getMood(moodId)
                    .subscribeOn(scheduler.io())

    override fun getAllMoods(): Flowable<List<MyMood>> =
            moodDao.getAllMoods()
                    .subscribeOn(scheduler.io())

    override fun isMoodEnabled(): Boolean = prefs.isMoodEnabled()
}