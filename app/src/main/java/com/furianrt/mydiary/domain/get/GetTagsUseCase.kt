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
import io.reactivex.Flowable
import javax.inject.Inject

class GetTagsUseCase @Inject constructor(
        private val tagGateway: TagGateway
) {

    fun invoke(noteId: String): Flowable<List<MyTag>> = tagGateway.getTagsForNote(noteId)

    fun invoke(): Flowable<List<MyTag>> = tagGateway.getAllTags()
}