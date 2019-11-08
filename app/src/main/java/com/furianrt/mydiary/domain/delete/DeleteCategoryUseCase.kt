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

import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.gateway.category.CategoryGateway
import io.reactivex.Completable
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
        private val categoryGateway: CategoryGateway
) {

    fun invoke(category: MyCategory): Completable =
            categoryGateway.deleteCategory(category)
}