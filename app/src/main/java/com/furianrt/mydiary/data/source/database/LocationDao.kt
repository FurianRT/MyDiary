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
import com.furianrt.mydiary.data.entity.MyLocation
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

    @Query("UPDATE Locations SET is_location_deleted = 1, location_sync_with = '[]' WHERE id_location IN (:locationIds)")
    fun delete(locationIds: List<String>): Completable

    @Query("SELECT * FROM Locations WHERE is_location_deleted = 0")
    fun getAllLocations(): Flowable<List<MyLocation>>

    @Query("SELECT * FROM Locations WHERE is_location_deleted = 1")
    fun getDeletedLocations(): Flowable<List<MyLocation>>

    @Query("DELETE FROM Locations WHERE is_location_deleted = 1")
    fun cleanup(): Completable
}