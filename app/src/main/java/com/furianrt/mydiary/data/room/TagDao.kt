package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyTag
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class TagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(tag: MyTag)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(tag: List<MyTag>)

    @Update
    abstract fun update(tag: MyTag)

    @Query("UPDATE Tags SET is_tag_deleted = 1 WHERE id_tag = :tagId")
    abstract fun delete(tagId: String)

    @Query("SELECT * FROM Tags WHERE is_tag_deleted = 1")
    abstract fun getDeletedTags(): Flowable<List<MyTag>>

    @Query("DELETE FROM Tags WHERE is_tag_deleted = 1")
    abstract fun cleanup()

    @Query("SELECT * FROM Tags WHERE is_tag_deleted = 0")
    abstract fun getAllTags(): Single<List<MyTag>>
}