package com.furianrt.mydiary.data.repository.category

import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface CategoryRepository {
    fun insertCategory(category: MyCategory): Completable
    fun insertCategory(categories: List<MyCategory>): Completable
    fun updateCategory(category: MyCategory): Completable
    fun updateCategoriesSync(categories: List<MyCategory>): Completable
    fun deleteCategory(category: MyCategory): Completable
    fun deleteCategoriesFromCloud(categories: List<MyCategory>): Completable
    fun cleanupCategories(): Completable
    fun getCategory(categoryId: String): Single<MyCategory>
    fun getAllCategories(): Flowable<List<MyCategory>>
    fun getDeletedCategories(): Flowable<List<MyCategory>>
    fun getAllCategoriesFromCloud(): Single<List<MyCategory>>
    fun saveCategoriesInCloud(categories: List<MyCategory>): Completable
}