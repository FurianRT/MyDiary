package com.furianrt.mydiary.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.furianrt.mydiary.data.model.MyImage
import io.reactivex.Flowable

@Dao
interface ImageDao {

    @Insert
    fun insert(image: MyImage)

    @Insert
    fun insert(images: List<MyImage>)

    @Query("SELECT * FROM Images WHERE id_note = :noteId")
    fun getImagesForNote(noteId: Long): Flowable<List<MyImage>>
}