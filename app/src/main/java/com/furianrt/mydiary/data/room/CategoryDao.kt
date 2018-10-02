package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CategoryDao {

    @Insert
    fun insert(category: MyCategory): Long

    @Update
    fun update(category: MyCategory)

    @Delete
    fun delete(category: MyCategory)

    @Query("SELECT * FrOM Categories WHERE id_category = :categoryId")
    fun getCategory(categoryId: Long): Single<MyCategory>

    @Query("SELECT * FROM Categories")
    fun getAllCategories(): Flowable<List<MyCategory>>
}