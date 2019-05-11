package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyForecast
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface ForecastDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(forecast: MyForecast): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(forecast: List<MyForecast>): Completable

    @Update
    fun update(forecasts: List<MyForecast>): Completable

    @Query("SELECT * FROM Forecasts WHERE is_forecast_deleted = 0")
    fun getAllForecasts(): Single<List<MyForecast>>
}