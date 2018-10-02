package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyTag
import io.reactivex.Single

@Dao
abstract class TagDao {

    @Insert
    abstract fun insert(tag: MyTag): Long

    @Update
    abstract fun update(tag: MyTag)

    @Delete
    abstract fun delete(tag: MyTag)

    @Query("SELECT * FROM Tags WHERE id_tag = :tagId")
    abstract fun getTag(tagId: Long): MyTag

    @Query("SELECT * FROM Tags")
    abstract fun getAllTags(): Single<List<MyTag>>
}