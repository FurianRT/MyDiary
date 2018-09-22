package com.furianrt.mydiary.data.room

import android.arch.persistence.room.*
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

    @Query("SELECT * FROM Images WHERE id_note = :noteId ORDER BY `order`")
    abstract fun getImagesForNote(noteId: String): Flowable<List<MyImage>>
}