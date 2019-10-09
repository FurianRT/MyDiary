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

import androidx.room.*
import com.furianrt.mydiary.data.entity.MyForecast
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface ForecastDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(forecast: MyForecast): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(forecast: List<MyForecast>): Completable

    @Update
    fun update(forecasts: List<MyForecast>): Completable

    @Query("UPDATE ${MyForecast.TABLE_NAME} SET ${MyForecast.FIELD_IS_DELETED} = 1, ${MyForecast.FIELD_SYNC_WITH} = '[]' WHERE ${MyForecast.FIELD_NOTE_ID} = :noteId")
    fun delete(noteId: String): Completable

    @Query("SELECT * FROM ${MyForecast.TABLE_NAME} WHERE ${MyForecast.FIELD_IS_DELETED} = 0")
    fun getAllForecasts(): Single<List<MyForecast>>

    @Query("SELECT * FROM ${MyForecast.TABLE_NAME} WHERE ${MyForecast.FIELD_IS_DELETED} = 1")
    fun getDeletedForecasts(): Flowable<List<MyForecast>>

    @Query("DELETE FROM ${MyForecast.TABLE_NAME} WHERE ${MyForecast.FIELD_IS_DELETED} = 1")
    fun cleanup(): Completable
}