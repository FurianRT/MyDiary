package com.furianrt.mydiary.data.repository.category

import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.CategoryDao
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.*
import javax.inject.Inject

class CategoryRepositoryImp @Inject constructor(
        private val categoryDao: CategoryDao,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : CategoryRepository {

    override fun insertCategory(category: MyCategory): Completable =
            categoryDao.insert(category)
                    .subscribeOn(rxScheduler)

    override fun insertCategory(categories: List<MyCategory>): Completable =
            categoryDao.insert(categories)
                    .subscribeOn(rxScheduler)

    override fun updateCategory(category: MyCategory): Completable =
            categoryDao.update(category)
                    .subscribeOn(rxScheduler)

    override fun updateCategoriesSync(categories: List<MyCategory>): Completable =
            categoryDao.update(categories)
                    .subscribeOn(rxScheduler)

    override fun deleteCategory(category: MyCategory): Completable =
            categoryDao.delete(category.id)
                    .subscribeOn(rxScheduler)

    override fun cleanupCategories(): Completable =
            categoryDao.cleanup()
                    .subscribeOn(rxScheduler)

    override fun getAllCategories(): Flowable<List<MyCategory>> =
            categoryDao.getAllCategories()
                    .subscribeOn(rxScheduler)

    override fun getCategory(categoryId: String): Single<MyCategory> =
            categoryDao.getCategory(categoryId)
                    .subscribeOn(rxScheduler)

    override fun getDeletedCategories(): Flowable<List<MyCategory>> =
            categoryDao.getDeletedCategories()
                    .subscribeOn(rxScheduler)

    override fun saveCategoriesInCloud(categories: List<MyCategory>): Completable =
            cloud.saveCategories(categories, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteCategoriesFromCloud(categories: List<MyCategory>): Completable =
            cloud.deleteCategories(categories, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getAllCategoriesFromCloud(): Single<List<MyCategory>> =
            cloud.getAllCategories(auth.getUserId())
                    .subscribeOn(rxScheduler)
}