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
import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.entity.MyNote
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: MyCategory): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: List<MyCategory>): Completable

    @Update
    fun update(category: MyCategory): Completable

    @Update
    fun update(category: List<MyCategory>): Completable

    @Query("UPDATE ${MyCategory.TABLE_NAME} SET ${MyCategory.FIELD_IS_DELETED} = 1, ${MyCategory.FIELD_SYNC_WITH} = '[]' WHERE ${MyCategory.FIELD_ID} = :categoryId")
    fun delete(categoryId: String): Completable

    @Query("DELETE FROM ${MyCategory.TABLE_NAME} WHERE ${MyCategory.FIELD_IS_DELETED} = 1")
    fun cleanup(): Completable

    @Query("SELECT ${MyCategory.TABLE_NAME}.* FROM ${MyCategory.TABLE_NAME} " +
            "INNER JOIN ${MyNote.TABLE_NAME} " +
            "ON ${MyCategory.TABLE_NAME}.${MyCategory.FIELD_ID} = ${MyNote.TABLE_NAME}.${MyNote.FIELD_CATEGORY} " +
            "AND ${MyNote.TABLE_NAME}.${MyNote.FIELD_ID} = :noteId " +
            "AND ${MyNote.TABLE_NAME}.${MyNote.FIELD_IS_DELETED} = 0 " +
            "WHERE ${MyCategory.TABLE_NAME}.${MyCategory.FIELD_IS_DELETED} = 0")
    fun getCategory(noteId: String): Flowable<List<MyCategory>>

    @Query("SELECT * FROM ${MyCategory.TABLE_NAME} WHERE ${MyCategory.FIELD_IS_DELETED} = 1")
    fun getDeletedCategories(): Flowable<List<MyCategory>>

    @Query("SELECT * FROM ${MyCategory.TABLE_NAME} WHERE ${MyCategory.FIELD_IS_DELETED} = 0")
    fun getAllCategories(): Flowable<List<MyCategory>>
}