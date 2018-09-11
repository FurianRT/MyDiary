package com.furianrt.mydiary.data.room

import android.arch.persistence.room.*
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.Flowable

@Dao
interface PackageDao {

    @Insert
    fun insert(pack: MyCategory)

    @Update
    fun update(pack: MyCategory)

    @Delete
    fun delete(pack: MyCategory)

    @Query("SELECT * FROM Categories")
    fun getAllCategories(): Flowable<List<MyCategory>>
}