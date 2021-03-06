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
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.furianrt.mydiary.model.entity.MyProfile
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface ProfileDao {

    @Insert
    fun insert(profile: MyProfile): Completable

    @Update
    fun update(profile: MyProfile): Completable

    @Query("DELETE FROM ${MyProfile.TABLE_NAME}")
    fun clearProfile(): Completable

    @Query("SELECT * FROM ${MyProfile.TABLE_NAME}")
    fun getProfiles(): Flowable<List<MyProfile>>

    @Query("SELECT COUNT(${MyProfile.FIELD_ID}) FROM ${MyProfile.TABLE_NAME}")
    fun getProfileCount(): Single<Int>
}