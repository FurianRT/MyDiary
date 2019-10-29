/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.database

import androidx.room.*
import com.furianrt.mydiary.model.entity.MyImage
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(image: MyImage): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(images: List<MyImage>): Completable

    @Update
    fun update(image: MyImage): Completable

    @Update
    fun update(images: List<MyImage>): Completable

    @Query("UPDATE ${MyImage.TABLE_NAME} SET ${MyImage.FIELD_IS_DELETED} = 1, ${MyImage.FIELD_SYNC_WITH} = '[]' WHERE ${MyImage.FIELD_ID_NOTE} = :noteId")
    fun deleteByNoteId(noteId: String): Completable

    @Query("UPDATE ${MyImage.TABLE_NAME} SET ${MyImage.FIELD_IS_DELETED} = 1, ${MyImage.FIELD_SYNC_WITH} = '[]' WHERE ${MyImage.FIELD_NAME} = :imageId")
    fun delete(imageId: String): Completable

    @Query("UPDATE ${MyImage.TABLE_NAME} SET ${MyImage.FIELD_IS_DELETED} = 1, ${MyImage.FIELD_SYNC_WITH} = '[]' WHERE ${MyImage.FIELD_NAME} IN (:imageIds)")
    fun delete(imageIds: List<String>): Completable

    @Query("SELECT * FROM ${MyImage.TABLE_NAME} WHERE ${MyImage.FIELD_IS_DELETED} = 0 ORDER BY ${MyImage.FIELD_ORDER}, ${MyImage.FIELD_ADDED_TIME}")
    fun getAllImages(): Flowable<List<MyImage>>

    @Query("SELECT * FROM ${MyImage.TABLE_NAME} WHERE ${MyImage.FIELD_IS_DELETED} = 1")
    fun getDeletedImages(): Flowable<List<MyImage>>

    @Query("SELECT * FROM ${MyImage.TABLE_NAME} " +
            "WHERE ${MyImage.FIELD_ID_NOTE} = :noteId AND ${MyImage.FIELD_IS_DELETED} = 0 " +
            "ORDER BY ${MyImage.FIELD_ORDER} ASC, ${MyImage.FIELD_ADDED_TIME} ASC")
    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>

    @Query("SELECT COUNT(*) FROM ${MyImage.TABLE_NAME} WHERE ${MyImage.FIELD_IS_DELETED} = 0")
    fun getCount(): Flowable<Int>

    @Query("DELETE FROM ${MyImage.TABLE_NAME} WHERE ${MyImage.FIELD_IS_DELETED} = 1")
    fun cleanup(): Completable
}