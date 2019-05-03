package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyTag
import io.reactivex.Flowable

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tag: MyTag)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tag: List<MyTag>)

    @Update
    fun update(tag: MyTag)

    @Update
    fun update(tags: List<MyTag>)

    @Query("UPDATE Tags SET is_tag_deleted = 1, tag_sync_with = '[]' WHERE id_tag = :tagId")
    fun delete(tagId: String)

    @Query("SELECT * FROM Tags WHERE is_tag_deleted = 1")
    fun getDeletedTags(): Flowable<List<MyTag>>

    @Query("DELETE FROM Tags WHERE is_tag_deleted = 1")
    fun cleanup()

    @Query("SELECT * FROM Tags WHERE is_tag_deleted = 0")
    fun getAllTags(): Flowable<List<MyTag>>
}