package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyImage
import io.reactivex.Flowable

@Dao
abstract class ImageDao {

    @Insert
    abstract fun insert(image: MyImage)

    @Insert
    abstract fun insert(images: List<MyImage>)

    @Update
    abstract fun update(image: MyImage)

    @Transaction
    open fun updateAll(images: List<MyImage>) {
        images.forEach { update(it) }
    }

    @Query("UPDATE Images SET is_image_deleted = 1 WHERE name IN (:imageIds)")
    abstract fun delete(imageIds: List<String>)

    @Query("SELECT * FROM Images " +
            "WHERE id_note_image = :noteId AND is_image_deleted = 0 " +
            "ORDER BY `order` ASC, time_added DESC")
    abstract fun getImagesForNote(noteId: String): Flowable<List<MyImage>>
}