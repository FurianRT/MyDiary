package com.furianrt.mydiary.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.furianrt.mydiary.data.model.MyTag
import io.reactivex.Single

@Dao
abstract class TagDao {

    @Insert
    abstract fun insert(tag: MyTag)

    @Update
    abstract fun update(tag: MyTag)

    @Query("UPDATE Tags SET is_tag_deleted = 1 WHERE id_tag = :tagId")
    abstract fun delete(tagId: String)

    @Query("SELECT * FROM Tags WHERE is_tag_deleted = 0")
    abstract fun getAllTags(): Single<List<MyTag>>
}