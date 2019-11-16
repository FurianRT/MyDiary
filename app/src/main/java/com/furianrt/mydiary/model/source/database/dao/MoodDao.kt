/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.furianrt.mydiary.model.entity.MyMood
import io.reactivex.Flowable

@Dao
interface MoodDao {

    @Query("SELECT * FROM ${MyMood.TABLE_NAME} WHERE ${MyMood.FIELD_ID} = :moodId")
    fun getMood(moodId: Int): Flowable<MyMood>

    @Query("SELECT * FROM ${MyMood.TABLE_NAME} ORDER BY ${MyMood.FIELD_ID} DESC")
    fun getAllMoods(): Flowable<List<MyMood>>
}