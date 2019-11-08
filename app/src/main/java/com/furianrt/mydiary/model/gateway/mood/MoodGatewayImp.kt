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
import com.furianrt.mydiary.model.source.database.MoodDao
import com.furianrt.mydiary.model.source.preferences.PreferencesHelper
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.Flowable
import javax.inject.Inject

class MoodGatewayImp @Inject constructor(
        private val moodDao: MoodDao,
        private val prefs: PreferencesHelper,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : MoodGateway {

    override fun getMood(moodId: Int): Flowable<MyMood> =
            moodDao.getMood(moodId)
                    .subscribeOn(scheduler.io())

    override fun getAllMoods(): Flowable<List<MyMood>> =
            moodDao.getAllMoods()
                    .subscribeOn(scheduler.io())

    override fun isMoodEnabled(): Boolean = prefs.isMoodEnabled()
}