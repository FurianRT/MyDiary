package com.furianrt.mydiary.data.room

import androidx.room.Dao
import androidx.room.Query
import com.furianrt.mydiary.data.model.MyMood
import io.reactivex.Single

@Dao
interface MoodDao {

    @Query("SELECT * FROM Moods WHERE id_mood = :moodId")
    fun getMood(moodId: Int): Single<MyMood>

    @Query("SELECT * FROM Moods ORDER BY id_mood DESC")
    fun getAllMoods(): Single<List<MyMood>>
}