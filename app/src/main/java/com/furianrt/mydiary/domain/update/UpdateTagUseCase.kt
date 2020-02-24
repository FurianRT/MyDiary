/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.update

import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import io.reactivex.Completable
import javax.inject.Inject

class UpdateTagUseCase @Inject constructor(
        private val tagGateway: TagGateway
) {

    class InvalidTagNameException : Throwable()

    operator fun invoke(tag: MyTag): Completable =
            tagGateway.getAllTags()
                    .first(emptyList())
                    .flatMapCompletable { tags ->
                        if (tags.find { it.name == tag.name } != null) {
                            throw InvalidTagNameException()
                        } else {
                            tagGateway.updateTag(tag.apply { syncWith.clear() })
                        }
                    }
}