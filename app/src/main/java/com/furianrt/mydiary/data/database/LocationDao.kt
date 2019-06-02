package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyLocation
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(location: MyLocation): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(location: List<MyLocation>): Completable

    @Update
    fun update(locations: List<MyLocation>): Completable

    @Query("UPDATE Locations SET is_location_deleted = 1, location_sync_with = '[]' WHERE name_location = :locationName")
    fun delete(locationName: String): Completable

    @Query("SELECT * FROM Locations WHERE is_location_deleted = 0")
    fun getAllLocations(): Flowable<List<MyLocation>>

    @Query("SELECT * FROM Locations WHERE is_location_deleted = 1")
    fun getDeletedLocations(): Flowable<List<MyLocation>>

    @Query("DELETE FROM Locations WHERE is_location_deleted = 1")
    fun cleanup(): Completable
}