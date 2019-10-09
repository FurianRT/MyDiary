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
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.furianrt.mydiary.data.entity.MyProfile
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface ProfileDao {

    @Insert
    fun insert(profile: MyProfile): Completable

    @Update
    fun update(profile: MyProfile): Completable

    @Query("DELETE FROM ${MyProfile.TABLE_NAME}")
    fun clearProfile(): Completable

    @Query("SELECT * FROM ${MyProfile.TABLE_NAME}")
    fun getProfile(): Observable<MyProfile>

    @Query("SELECT COUNT(${MyProfile.FIELD_ID}) FROM ${MyProfile.TABLE_NAME}")
    fun getProfileCount(): Single<Int>
}