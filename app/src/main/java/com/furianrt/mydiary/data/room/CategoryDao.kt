package com.furianrt.mydiary.data.room

import androidx.room.*
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(category: MyCategory)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(category: List<MyCategory>)

    @Update
    fun update(category: MyCategory)

    @Update
    fun updateSync(category: List<MyCategory>)

    @Query("UPDATE Categories SET is_category_deleted = 1 WHERE id_category = :categoryId")
    fun delete(categoryId: String)

    @Query("DELETE FROM Categories WHERE is_category_deleted = 1")
    fun cleanup()

    @Query("SELECT * FROM Categories WHERE id_category = :categoryId AND is_category_deleted = 0")
    fun getCategory(categoryId: String): Maybe<MyCategory>

    @Query("SELECT * FROM Categories WHERE is_category_deleted = 1")
    fun getDeletedCategories(): Flowable<List<MyCategory>>

    @Query("SELECT * FROM Categories WHERE is_category_deleted = 0")
    fun getAllCategories(): Flowable<List<MyCategory>>
}