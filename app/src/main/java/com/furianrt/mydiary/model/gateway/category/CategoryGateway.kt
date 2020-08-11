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
import com.google.common.base.Optional
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface CategoryGateway {
    fun insertCategory(category: MyCategory): Completable
    fun insertCategory(categories: List<MyCategory>): Completable
    fun updateCategory(category: MyCategory): Completable
    fun updateCategoriesSync(categories: List<MyCategory>): Completable
    fun deleteCategory(category: MyCategory): Completable
    fun deleteCategoriesFromCloud(categories: List<MyCategory>): Completable
    fun cleanupCategories(): Completable
    fun getCategory(noteId: String): Flowable<Optional<MyCategory>>
    fun getAllCategories(): Flowable<List<MyCategory>>
    fun getDeletedCategories(): Flowable<List<MyCategory>>
    fun getAllCategoriesFromCloud(): Single<List<MyCategory>>
    fun saveCategoriesInCloud(categories: List<MyCategory>): Completable
}