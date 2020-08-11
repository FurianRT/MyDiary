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

import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class GetTagsUseCase @Inject constructor(
        private val tagGateway: TagGateway
) {

    operator fun invoke(noteId: String): Flowable<List<MyTag>> = tagGateway.getTagsForNote(noteId)

    operator fun invoke(): Flowable<List<MyTag>> = tagGateway.getAllTags()
}