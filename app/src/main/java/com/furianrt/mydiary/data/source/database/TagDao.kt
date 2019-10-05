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
import com.furianrt.mydiary.data.entity.MyTag
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tag: MyTag): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tag: List<MyTag>): Completable

    @Update
    fun update(tag: MyTag): Completable

    @Update
    fun update(tags: List<MyTag>): Completable

    @Query("UPDATE Tags SET is_tag_deleted = 1, tag_sync_with = '[]' WHERE id_tag = :tagId")
    fun delete(tagId: String): Completable

    @Query("SELECT * FROM Tags WHERE is_tag_deleted = 1")
    fun getDeletedTags(): Flowable<List<MyTag>>

    @Query("DELETE FROM Tags WHERE is_tag_deleted = 1")
    fun cleanup(): Completable

    @Query("SELECT * FROM Tags WHERE is_tag_deleted = 0")
    fun getAllTags(): Flowable<List<MyTag>>
}