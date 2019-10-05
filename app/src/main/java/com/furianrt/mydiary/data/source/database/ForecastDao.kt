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

    @Query("UPDATE Forecasts SET is_forecast_deleted = 1, forecast_sync_with = '[]' WHERE note_id = :noteId")
    fun delete(noteId: String): Completable

    @Query("SELECT * FROM Forecasts WHERE is_forecast_deleted = 0")
    fun getAllForecasts(): Single<List<MyForecast>>

    @Query("SELECT * FROM Forecasts WHERE is_forecast_deleted = 1")
    fun getDeletedForecasts(): Flowable<List<MyForecast>>

    @Query("DELETE FROM Forecasts WHERE is_forecast_deleted = 1")
    fun cleanup(): Completable
}