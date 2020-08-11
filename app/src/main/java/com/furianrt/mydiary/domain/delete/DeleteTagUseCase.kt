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

import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
        private val tagGateway: TagGateway
) {

    operator fun invoke(tag: MyTag): Completable = tagGateway.deleteTag(tag)
}