package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyLocation
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(location: MyLocation): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(location: List<MyLocation>): Completable

    @Update
    fun update(locations: List<MyLocation>): Completable

    @Query("UPDATE Locations SET is_location_deleted = 1, location_sync_with = '[]' WHERE name_location = :locationId")
    fun delete(locationId: String): Completable

    @Query("SELECT * FROM Locations WHERE is_location_deleted = 0")
    fun getAllLocations(): Single<List<MyLocation>>
}