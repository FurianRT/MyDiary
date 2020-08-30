/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.category

import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.source.auth.AuthSource
import com.furianrt.mydiary.model.source.cloud.CloudSource
import com.furianrt.mydiary.model.source.database.dao.CategoryDao
import com.furianrt.mydiary.utils.MyRxUtils
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CategoryGatewayImp @Inject constructor(
        private val categoryDao: CategoryDao,
        private val cloud: CloudSource,
        private val auth: AuthSource,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : CategoryGateway {

    override fun insertCategory(category: MyCategory): Completable =
            categoryDao.insert(category)
                    .subscribeOn(scheduler.io())

    override fun insertCategory(categories: List<MyCategory>): Completable =
            categoryDao.insert(categories)
                    .subscribeOn(scheduler.io())

    override fun updateCategory(category: MyCategory): Completable =
            categoryDao.update(category)
                    .subscribeOn(scheduler.io())

    override fun updateCategoriesSync(categories: List<MyCategory>): Completable =
            categoryDao.update(categories)
                    .subscribeOn(scheduler.io())

    override fun deleteCategory(category: MyCategory): Completable =
            categoryDao.delete(category.id)
                    .subscribeOn(scheduler.io())

    override fun cleanupCategories(): Completable =
            categoryDao.cleanup()
                    .subscribeOn(scheduler.io())

    override fun getAllCategories(): Flowable<List<MyCategory>> =
            categoryDao.getAllCategories()
                    .subscribeOn(scheduler.io())

    override fun getCategory(noteId: String): Flowable<Optional<MyCategory>> =
            categoryDao.getCategory(noteId)
                    .map { Optional.fromNullable(it.firstOrNull()) }
                    .subscribeOn(scheduler.io())

    override fun getDeletedCategories(): Flowable<List<MyCategory>> =
            categoryDao.getDeletedCategories()
                    .subscribeOn(scheduler.io())

    override fun saveCategoriesInCloud(categories: List<MyCategory>): Completable =
            cloud.saveCategories(categories, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun deleteCategoriesFromCloud(categories: List<MyCategory>): Completable =
            cloud.deleteCategories(categories, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getAllCategoriesFromCloud(): Single<List<MyCategory>> =
            cloud.getAllCategories(auth.getUserId())
                    .subscribeOn(scheduler.io())
}