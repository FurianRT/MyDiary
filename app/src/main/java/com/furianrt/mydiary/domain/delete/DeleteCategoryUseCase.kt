/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.delete

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.repository.category.CategoryRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
        private val categoryRepository: CategoryRepository
) {

    fun invoke(category: MyCategory): Completable =
            categoryRepository.deleteCategory(category)
}