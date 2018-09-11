package com.furianrt.mydiary.data.room

import android.arch.persistence.room.*
import com.furianrt.mydiary.data.model.MyTag
import io.reactivex.Single

@Dao
interface TagDao {

    @Insert
    fun insert(tag: MyTag): Long

    @Update
    fun update(tag: MyTag)

    @Delete
    fun delete(tag: MyTag)

    @Query("SELECT * FROM Tags")
    fun getAllTags(): Single<List<MyTag>>
}