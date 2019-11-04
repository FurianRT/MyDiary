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

import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Single
import javax.inject.Inject

class SaveTagUseCase @Inject constructor(
        private val tagGateway: TagGateway
) {

    class InvalidTagNameException : Throwable()

    fun invoke(name: String): Single<String> {
        val id = generateUniqueId()
        return tagGateway.getAllTags()
                .first(emptyList())
                .flatMapCompletable { tags ->
                    if (tags.find { it.name == name } != null) {
                        throw InvalidTagNameException()
                    } else {
                        tagGateway.insertTag(MyTag(id, name))
                    }
                }
                .andThen(Single.just(id))
    }
}