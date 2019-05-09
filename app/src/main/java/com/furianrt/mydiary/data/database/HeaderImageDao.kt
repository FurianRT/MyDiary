package com.furianrt.mydiary.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furianrt.mydiary.data.model.MyHeaderImage
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface HeaderImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(headerImage: MyHeaderImage): Completable

    @Query("SELECT * FROM HeaderImages")
    fun getHeaderImages(): Flowable<List<MyHeaderImage>>
}