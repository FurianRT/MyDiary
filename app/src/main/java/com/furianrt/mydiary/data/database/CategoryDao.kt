package com.furianrt.mydiary.data.database

import androidx.room.*
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: MyCategory): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: List<MyCategory>): Completable

    @Update
    fun update(category: MyCategory): Completable

    @Update
    fun update(category: List<MyCategory>): Completable

    @Query("UPDATE Categories SET is_category_deleted = 1, category_sync_with = '[]' WHERE id_category = :categoryId")
    fun delete(categoryId: String): Completable

    @Query("DELETE FROM Categories WHERE is_category_deleted = 1")
    fun cleanup(): Completable

    @Query("SELECT * FROM Categories WHERE id_category = :categoryId AND is_category_deleted = 0")
    fun getCategory(categoryId: String): Single<MyCategory>

    @Query("SELECT * FROM Categories WHERE is_category_deleted = 1")
    fun getDeletedCategories(): Flowable<List<MyCategory>>

    @Query("SELECT * FROM Categories WHERE is_category_deleted = 0")
    fun getAllCategories(): Flowable<List<MyCategory>>
}