package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyForecast
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

    @Query("UPDATE Forecasts SET is_forecast_deleted = 1, forecast_sync_with = '[]' WHERE note_id = :note_id")
    fun delete(note_id: String): Completable

    @Query("SELECT * FROM Forecasts WHERE is_forecast_deleted = 0")
    fun getAllForecasts(): Single<List<MyForecast>>

    @Query("SELECT * FROM Forecasts WHERE is_forecast_deleted = 1")
    fun getDeletedForecasts(): Flowable<List<MyForecast>>

    @Query("DELETE FROM Forecasts WHERE is_forecast_deleted = 1")
    fun cleanup(): Completable
}