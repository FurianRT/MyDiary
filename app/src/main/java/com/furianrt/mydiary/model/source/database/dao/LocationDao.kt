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

import androidx.room.*
import com.furianrt.mydiary.model.entity.MyLocation
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(location: MyLocation): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(location: List<MyLocation>): Completable

    @Update
    fun update(locations: List<MyLocation>): Completable

    @Query("UPDATE ${MyLocation.TABLE_NAME} SET ${MyLocation.FIELD_IS_DELETED} = 1, ${MyLocation.FIELD_SYNC_WITH} = '[]' WHERE ${MyLocation.FIELD_ID} IN (:locationIds)")
    fun delete(locationIds: List<String>): Completable

    @Query("SELECT * FROM ${MyLocation.TABLE_NAME} WHERE ${MyLocation.FIELD_IS_DELETED} = 0")
    fun getAllLocations(): Flowable<List<MyLocation>>

    @Query("SELECT * FROM ${MyLocation.TABLE_NAME} WHERE ${MyLocation.FIELD_IS_DELETED} = 1")
    fun getDeletedLocations(): Flowable<List<MyLocation>>

    @Query("DELETE FROM ${MyLocation.TABLE_NAME} WHERE ${MyLocation.FIELD_IS_DELETED} = 1")
    fun cleanup(): Completable
}