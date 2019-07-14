package com.furianrt.mydiary.data.repository.mood

import com.furianrt.mydiary.data.model.MyMood
import io.reactivex.Single

interface MoodRepository {
    fun getMood(moodId: Int): Single<MyMood>
    fun getAllMoods(): Single<List<MyMood>>
    fun isMoodEnabled(): Boolean
}