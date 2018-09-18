package com.furianrt.mydiary.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.furianrt.mydiary.data.model.MyHeaderImage
import io.reactivex.Flowable

@Dao
interface HeaderImageDao {

    @Insert
    fun insert(headerImage: MyHeaderImage)

    @Insert
    fun insert(headerImages: List<MyHeaderImage>)

    @Query("SELECT * FROM HeaderImages")
    fun getHeaderImages(): Flowable<List<MyHeaderImage>>
}