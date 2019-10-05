/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.data.source.database

import androidx.room.Dao
import androidx.room.Query
import com.furianrt.mydiary.data.entity.MyMood
import io.reactivex.Single

@Dao
interface MoodDao {

    @Query("SELECT * FROM Moods WHERE id_mood = :moodId")
    fun getMood(moodId: Int): Single<MyMood>

    @Query("SELECT * FROM Moods ORDER BY id_mood DESC")
    fun getAllMoods(): Single<List<MyMood>>
}