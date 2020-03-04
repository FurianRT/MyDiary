/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.sync

import com.furianrt.mydiary.model.gateway.category.CategoryGateway
import io.reactivex.Completable
import javax.inject.Inject

class SyncCategoriesUseCase @Inject constructor(
        private val categoryGateway: CategoryGateway
) {

    class SyncCategoriesException : Throwable()

    operator fun invoke(email: String): Completable =
            categoryGateway.getAllCategories()
                    .first(emptyList())
                    .map { categories -> categories.filter { !it.isSync(email) } }
                    .map { categories -> categories.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { categories ->
                        Completable.concat(listOf(
                                categoryGateway.saveCategoriesInCloud(categories),
                                categoryGateway.updateCategoriesSync(categories)
                        ))
                    }
                    .andThen(categoryGateway.getDeletedCategories().first(emptyList()))
                    .flatMapCompletable { categoryGateway.deleteCategoriesFromCloud(it) }
                    .andThen(categoryGateway.getAllCategoriesFromCloud())
                    .flatMapCompletable { categoryGateway.insertCategory(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncCategoriesException())
                    }
}