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

import com.furianrt.mydiary.model.entity.pojo.TagsAndAppearance
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class GetTagsWithAppearanceUseCase @Inject constructor(
        private val tagGateway: TagGateway,
        private val getAppearanceUseCase: GetAppearanceUseCase
) {

    operator fun invoke(noteId: String): Flowable<TagsAndAppearance> =
            Flowable.combineLatest(
                    tagGateway.getTagsForNote(noteId),
                    getAppearanceUseCase(noteId),
                    { tags, appearance -> TagsAndAppearance(tags, appearance.orNull()) }
            )
}