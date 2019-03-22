package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyImage
import io.reactivex.Flowable

@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(image: MyImage)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(images: List<MyImage>)

    @Update
    fun update(images: List<MyImage>)

    @Query("UPDATE Images SET is_image_deleted = 1, is_image_sync = 0 WHERE id_note_image = :noteId")
    fun deleteByNoteId(noteId: String)

    @Query("UPDATE Images SET is_image_deleted = 1, is_image_sync = 0 WHERE name = :imageId")
    fun delete(imageId: String)

    @Query("UPDATE Images SET is_image_deleted = 1, is_image_sync = 0 WHERE name IN (:imageIds)")
    fun delete(imageIds: List<String>)

    @Query("SELECT * FROM Images WHERE is_image_deleted = 0")
    fun getAllImages(): Flowable<List<MyImage>>

    @Query("SELECT * FROM Images WHERE is_image_deleted = 1")
    fun getDeletedImages(): Flowable<List<MyImage>>

    @Query("SELECT * FROM Images " +
            "WHERE id_note_image = :noteId AND is_image_deleted = 0 " +
            "ORDER BY image_order ASC, time_added DESC")
    fun getImagesForNote(noteId: String): Flowable<List<MyImage>>

    @Query("DELETE FROM Images WHERE is_image_deleted = 1")
    fun cleanup()
}