package com.furianrt.mydiary.data.repository.mood

import com.furianrt.mydiary.data.database.NoteDatabase
import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class MoodRepositoryImp @Inject constructor(
        private val database: NoteDatabase,
        private val prefs: PreferencesHelper,
        private val rxScheduler: Scheduler
) : MoodRepository {

    override fun getMood(moodId: Int): Single<MyMood> =
            database.moodDao()
                    .getMood(moodId)
                    .subscribeOn(rxScheduler)

    override fun getAllMoods(): Single<List<MyMood>> =
            database.moodDao()
                    .getAllMoods()
                    .subscribeOn(rxScheduler)

    override fun isMoodEnabled(): Boolean = prefs.isMoodEnabled()
}