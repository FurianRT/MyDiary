/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.gateway.category.CategoryGateway
import com.google.common.base.Optional
import io.reactivex.Flowable
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
        private val categoryGateway: CategoryGateway
) {

    operator fun invoke(): Flowable<List<MyCategory>> = categoryGateway.getAllCategories()

    operator fun invoke(noteId: String): Flowable<Optional<MyCategory>> = categoryGateway.getCategory(noteId)
}