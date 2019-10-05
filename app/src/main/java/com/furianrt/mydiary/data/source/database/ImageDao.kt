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
import com.furianrt.mydiary.data.entity.MyImage
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

    @Query("UPDATE Images SET is_image_deleted = 1, image_sync_with = '[]' WHERE id_note_image = :noteId")
    fun deleteByNoteId(noteId: String): Completable

    @Query("UPDATE Images SET is_image_deleted = 1, image_sync_with = '[]' WHERE name = :imageId")
    fun delete(imageId: String): Completable

    @Query("UPDATE Images SET is_image_deleted = 1, image_sync_with = '[]' WHERE name IN (:imageIds)")
    fun delete(imageIds: List<String>): Completable

    @Query("SELECT * FROM Images WHERE is_image_deleted = 0 ORDER BY image_order, time_added")
    fun getAllImages(): Flowable<List<MyImage>>

    @Query("SELECT * FROM Images WHERE is_image_deleted = 1")
    fun getDeletedImages(): Flowable<List<MyImage>>

    @Query("SELECT * FROM Images " +
            "WHERE id_note_image = :noteId AND is_image_deleted = 0 " +
            "ORDER BY image_order ASC, time_added DESC")
    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>

    @Query("SELECT COUNT(*) FROM Images WHERE is_image_deleted = 0")
    fun getCount(): Flowable<Int>

    @Query("DELETE FROM Images WHERE is_image_deleted = 1")
    fun cleanup(): Completable
}