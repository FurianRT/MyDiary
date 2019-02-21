package com.furianrt.mydiary.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface CategoryDao {

    @Insert
    fun insert(category: MyCategory): Long

    @Update
    fun update(category: MyCategory)

    @Query("UPDATE Categories SET is_category_deleted = 1 WHERE id_category = :categoryId")
    fun delete(categoryId: String)

    @Query("SELECT * FROM Categories WHERE id_category = :categoryId AND is_category_deleted = 0")
    fun getCategory(categoryId: Long): Maybe<MyCategory>

    @Query("SELECT * FROM Categories WHERE is_category_deleted = 0")
    fun getAllCategories(): Flowable<List<MyCategory>>
}