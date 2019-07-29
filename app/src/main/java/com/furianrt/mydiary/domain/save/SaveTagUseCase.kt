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

import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.repository.tag.TagRepository
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Single
import javax.inject.Inject

class SaveTagUseCase @Inject constructor(
        private val tagRepository: TagRepository
) {

    class InvalidTagNameException : Throwable()

    fun invoke(name: String): Single<String> {
        val id = generateUniqueId()
        return tagRepository.getAllTags()
                .first(emptyList())
                .flatMapCompletable { tags ->
                    if (tags.find { it.name == name } != null) {
                        throw InvalidTagNameException()
                    } else {
                        tagRepository.insertTag(MyTag(id, name))
                    }
                }
                .andThen(Single.just(id))
    }
}