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
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furianrt.mydiary.model.entity.MyHeaderImage
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface HeaderImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(headerImage: MyHeaderImage): Completable

    @Query("SELECT * FROM ${MyHeaderImage.TABLE_NAME}")
    fun getHeaderImages(): Flowable<List<MyHeaderImage>>
}