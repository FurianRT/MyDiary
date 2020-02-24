/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.save

import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.gateway.category.CategoryGateway
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Single
import javax.inject.Inject

class SaveCategoryUseCase @Inject constructor(
        private val categoryGateway: CategoryGateway
) {

    operator fun invoke(name: String, color: Int): Single<String> =
            with(generateUniqueId()) {
                categoryGateway.insertCategory(MyCategory(this, name, color))
                        .toSingleDefault(this)
            }
}