package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyImage
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

    @Query("SELECT * FROM Images WHERE is_image_deleted = 0")
    fun getAllImages(): Flowable<List<MyImage>>

    @Query("SELECT * FROM Images WHERE is_image_deleted = 1")
    fun getDeletedImages(): Flowable<List<MyImage>>

    @Query("SELECT * FROM Images " +
            "WHERE id_note_image = :noteId AND is_image_deleted = 0 " +
            "ORDER BY image_order ASC, time_added DESC")
    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>

    @Query("DELETE FROM Images WHERE is_image_deleted = 1")
    fun cleanup(): Completable
}