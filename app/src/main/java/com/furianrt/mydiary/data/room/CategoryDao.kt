package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface CategoryDao {

    @Insert
    fun insert(category: MyCategory): Long

    @Update
    fun update(category: MyCategory)

    @Delete
    fun delete(category: MyCategory)

    @Query("SELECT * FROM Categories WHERE id_category = :categoryId")
    fun getCategory(categoryId: Long): Maybe<MyCategory>

    @Query("SELECT * FROM Categories")
    fun getAllCategories(): Flowable<List<MyCategory>>
}