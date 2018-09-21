package com.furianrt.mydiary.data.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.furianrt.mydiary.data.model.MyHeaderImage
import io.reactivex.Flowable

@Dao
interface HeaderImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(headerImage: MyHeaderImage)

    @Insert
    fun insert(headerImages: List<MyHeaderImage>)

    @Query("DELETE FROM HeaderImages")
    fun deleteAll()

    @Query("SELECT * FROM HeaderImages")
    fun getHeaderImages(): Flowable<List<MyHeaderImage>>
}